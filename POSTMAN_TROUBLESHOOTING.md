# Postman Testing Guide for Fenix Commerce

## Issue: Internal Server Error (500)

When you receive an **Internal Server Error (500)** in Postman, it typically means:
1. The server received your request
2. Something went wrong during processing (database, validation, missing data, etc.)

## Root Cause Analysis

Based on your FenixCommerce project structure, the most common causes are:

### 1. Missing Database Prerequisites

Your application requires:
- **MySQL Database**: `logistics_platform`
- **Tables**: `organizations`, `websites`, `orders`, `fulfillments`, `tenants`
- **Data**: At least one organization and website record must exist

### 2. Foreign Key Constraints

When creating orders, the following must exist in the database:
- `orgId` must reference a valid record in `organizations` or `tenants` table
- `websiteId` must reference a valid record in `websites` table

---

## Solution: Test in Correct Order

### Test 1: Health Check (Verify Server is Running)

**Request:**
```
GET http://localhost:8080/api/health
```

**Expected Response (200 OK):**
```json
{
  "status": "UP",
  "timestamp": "2026-01-21T11:10:00Z",
  "application": "Fenix Commerce Platform",
  "version": "1.0.0"
}
```

**If this fails:**
- Your Spring Boot application is not running
- Start it with: `mvn spring-boot:run` or run from your IDE

---

### Test 2: Create Order (Most Common Issue)

**Common Error Scenario:**
```
POST http://localhost:8080/api/orders
Content-Type: application/json

{
  "orderId": "550e8400-e29b-41d4-a716-446655440000",
  "websiteId": "660e8400-e29b-41d4-a716-446655440001",
  "externalOrderId": "SHOPIFY-12345",
  "status": "CREATED",
  "financialStatus": "PAID",
  "customerEmail": "customer@example.com",
  "orderTotal": 129.99,
  "currency": "USD"
}
```

**Error Response (500):**
```json
{
  "timestamp": "2026-01-21T11:15:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/orders"
}
```

**Diagnosis:**
1. Check your Spring Boot console/logs for the actual error
2. Common issues:
   - `orderId` doesn't exist in database
   - `websiteId` doesn't exist in database
   - Database connection failed
   - Missing required field

---

## Correct Testing Workflow

### Step 1: Verify Database Setup

Run these SQL commands in MySQL:

```sql
-- Check if database exists
SHOW DATABASES LIKE 'logistics_platform';

-- Select the database
USE logistics_platform;

-- Check if tables exist
SHOW TABLES;

-- Check if organization/tenant data exists
SELECT * FROM tenants LIMIT 5;
SELECT * FROM organizations LIMIT 5;
SELECT * FROM websites LIMIT 5;
```

### Step 2: Insert Sample Data (If Needed)

If you don't have any organizations or tenants, you need to insert them first:

```sql
-- Insert a sample tenant
INSERT INTO tenants (tenant_id, tenant_name, created_at, updated_at)
VALUES (
  UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440000', '-', '')),
  'Sample Org',
  NOW(),
  NOW()
);

-- Insert a sample website (if needed)
-- Check your schema first to see what columns are required
```

### Step 3: Use Valid IDs in Postman

Once you have data in the database, copy the actual UUIDs:

```sql
-- Get valid tenant/org ID
SELECT HEX(tenant_id) as tenant_id_hex, tenant_name FROM tenants;

-- Get valid website ID (if you have a websites table)
SELECT HEX(website_id) as website_id_hex, website_name FROM websites;
```

Then use these IDs in your Postman request.

---

## Working Example (After Database Setup)

Let's assume you ran the queries and got:
- Tenant ID: `550e8400-e29b-41d4-a716-446655440000`
- Website ID: `660e8400-e29b-41d4-a716-446655440001`

### Create Order Request:

```
POST http://localhost:8080/api/orders
Content-Type: application/json

{
  "orgId": "550e8400-e29b-41d4-a716-446655440000",
  "websiteId": "660e8400-e29b-41d4-a716-446655440001",
  "externalOrderId": "TEST-ORDER-001",
  "externalOrderNumber": "ORD-2024-001",
  "status": "CREATED",
  "financialStatus": "PAID",
  "fulfillmentStatus": "UNFULFILLED",
  "customerEmail": "test@example.com",
  "orderTotal": 99.99,
  "currency": "USD"
}
```

### Expected Success Response (201 Created):

```json
{
  "orderId": "770e8400-e29b-41d4-a716-446655440002",
  "orgId": "550e8400-e29b-41d4-a716-446655440000",
  "websiteId": "660e8400-e29b-41d4-a716-446655440001",
  "externalOrderId": "TEST-ORDER-001",
  "externalOrderNumber": "ORD-2024-001",
  "status": "CREATED",
  "financialStatus": "PAID",
  "fulfillmentStatus": "UNFULFILLED",
  "customerEmail": "test@example.com",
  "orderTotal": 99.99,
  "currency": "USD",
  "createdAt": "2026-01-21T11:20:00.000Z",
  "updatedAt": "2026-01-21T11:20:00.000Z"
}
```

**IMPORTANT:** Save the `orderId` from this response!

---

### Create Fulfillment Request:

Now use the `orderId` you just received:

```
POST http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002/fulfillments
Content-Type: application/json

{
  "externalFulfillmentId": "SHIP-001",
  "status": "SHIPPED",
  "carrier": "FedEx",
  "serviceLevel": "2-Day",
  "shippedAt": "2026-01-21T10:30:00"
}
```

### Expected Success Response (201 Created):

```json
{
  "fulfillmentId": "880e8400-e29b-41d4-a716-446655440003",
  "orderId": "770e8400-e29b-41d4-a716-446655440002",
  "externalFulfillmentId": "SHIP-001",
  "status": "SHIPPED",
  "carrier": "FedEx",
  "serviceLevel": "2-Day",
  "shippedAt": "2026-01-21T10:30:00",
  "createdAt": "2026-01-21T11:25:00.000Z",
  "updatedAt": "2026-01-21T11:25:00.000Z"
}
```

---

## Debugging Tips

### 1. Check Spring Boot Console/Logs

When you get a 500 error, **ALWAYS** check your Spring Boot application console. It will show:
- The exact exception
- Stack trace
- SQL errors (if database-related)

Look for messages like:
- `ConstraintViolationException` - Missing or invalid foreign key
- `DataIntegrityViolationException` - Database constraint violation
- `SQLSyntaxErrorException` - SQL syntax error
- `JDBCConnectionException` - Database connection failed

### 2. Enable More Detailed Logging

Your `application.yml` already has debug logging enabled:
```yaml
logging:
  level:
    com.fenix.commerce: DEBUG
    org.hibernate.SQL: DEBUG
```

This will show all SQL queries being executed.

### 3. Test with Minimal Payload First

Start with the minimum required fields:

**Minimal Order:**
```json
{
  "orgId": "550e8400-e29b-41d4-a716-446655440000",
  "websiteId": "660e8400-e29b-41d4-a716-446655440001",
  "externalOrderId": "MINIMAL-TEST-001"
}
```

**Minimal Fulfillment:**
```json
{
  "externalFulfillmentId": "MINIMAL-SHIP-001"
}
```

Once these work, add more fields incrementally.

### 4. Verify Database Connection

Check if your Spring Boot app can connect to MySQL:

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/logistics_platform?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  username: root
  password: root123
```

Ensure:
- MySQL is running on port 3306
- Database `logistics_platform` exists
- Username `root` with password `root123` has access

---

## Common Postman Configuration Mistakes

### Mistake 1: Wrong URL
❌ `http://localhost:8080/orders`
✅ `http://localhost:8080/api/orders`

**Note the `/api` prefix** - it's defined in `application.yml` as `server.servlet.context-path: /api`

### Mistake 2: Missing Content-Type Header
For POST/PUT/PATCH requests, you **MUST** include:
```
Content-Type: application/json
```

### Mistake 3: Invalid JSON
```json
{
  "orgId": "550e8400-e29b-41d4-a716-446655440000",  // ❌ No comments in JSON
  "externalOrderId": 'TEST-001'  // ❌ Use double quotes, not single
}
```

**Correct:**
```json
{
  "orgId": "550e8400-e29b-41d4-a716-446655440000",
  "externalOrderId": "TEST-001"
}
```

### Mistake 4: Wrong UUID Format
❌ `550e8400e29b41d4a716446655440000` (no dashes)
✅ `550e8400-e29b-41d4-a716-446655440000` (with dashes)

---

## Quick Checklist

Before testing in Postman, verify:

- [ ] Spring Boot application is running (check console)
- [ ] MySQL is running
- [ ] Database `logistics_platform` exists
- [ ] Tables are created (check with `SHOW TABLES;`)
- [ ] At least one tenant/organization record exists
- [ ] At least one website record exists (if required)
- [ ] Using correct base URL: `http://localhost:8080/api`
- [ ] Content-Type header is set for POST/PUT/PATCH
- [ ] JSON payload is valid (use a JSON validator)
- [ ] UUIDs are in correct format with dashes

---

## Still Getting Errors?

1. **Copy the full error from Spring Boot console** and share it
2. **Run this SQL query** and share the results:
   ```sql
   USE logistics_platform;
   SHOW TABLES;
   SELECT COUNT(*) FROM tenants;
   SELECT COUNT(*) FROM orders;
   ```
3. **Share your Postman request**:
   - URL
   - Headers
   - Body

This will help diagnose the exact issue!
