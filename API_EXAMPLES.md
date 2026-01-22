# Fenix Commerce API - Postman Examples

## Server Configuration
- **Base URL**: `http://localhost:8080/api`
- **Port**: `8080`
- **Context Path**: `/api`

## Common Headers
All POST, PUT, and PATCH requests require:
```
Content-Type: application/json
```

---

## 1. Health Check

### Endpoint
```
GET http://localhost:8080/api/health
```

### Example Response
```json
{
  "status": "UP",
  "timestamp": "2026-01-21T11:10:00Z",
  "application": "Fenix Commerce Platform",
  "version": "1.0.0"
}
```

---

## 2. Create Order

### Endpoint
```
POST http://localhost:8080/api/orders
```

### Headers
```
Content-Type: application/json
```

### Request Body (JSON)
```json
{
  "orgId": "550e8400-e29b-41d4-a716-446655440000",
  "websiteId": "660e8400-e29b-41d4-a716-446655440001",
  "externalOrderId": "SHOPIFY-12345",
  "externalOrderNumber": "ORD-2024-001",
  "status": "CREATED",
  "financialStatus": "PAID",
  "fulfillmentStatus": "UNFULFILLED",
  "customerEmail": "customer@example.com",
  "orderTotal": 129.99,
  "currency": "USD"
}
```

### Valid Enum Values
- **status**: `CREATED`, `PROCESSING`, `CLOSED`, `CANCELLED`
- **financialStatus**: `PENDING`, `PAID`, `PARTIALLY_PAID`, `REFUNDED`, `PARTIALLY_REFUNDED`
- **fulfillmentStatus**: `UNFULFILLED`, `PARTIALLY_FULFILLED`, `FULFILLED`

### Response (201 Created)
```json
{
  "orderId": "770e8400-e29b-41d4-a716-446655440002",
  "orgId": "550e8400-e29b-41d4-a716-446655440000",
  "websiteId": "660e8400-e29b-41d4-a716-446655440001",
  "externalOrderId": "SHOPIFY-12345",
  "status": "CREATED",
  "financialStatus": "PAID",
  "customerEmail": "customer@example.com",
  "orderTotal": 129.99,
  "currency": "USD",
  "createdAt": "2026-01-21T11:10:00Z",
  "updatedAt": "2026-01-21T11:10:00Z"
}
```

---

## 3. Get Order by ID

### Endpoint
```
GET http://localhost:8080/api/orders/{orderId}
```

### Example
```
GET http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002
```

---

## 4. Create Fulfillment

### Endpoint
```
POST http://localhost:8080/api/orders/{orderId}/fulfillments
```

### IMPORTANT
- **Replace `{orderId}` with an actual Order UUID** that exists in your database
- You must create an Order first before you can create a Fulfillment

### Example Full URL
```
POST http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002/fulfillments
```

### Headers
```
Content-Type: application/json
```

### Request Body (JSON) - Minimal Required
```json
{
  "externalFulfillmentId": "SHIP-001"
}
```

### Request Body (JSON) - Complete Example
```json
{
  "externalFulfillmentId": "SHIP-001",
  "status": "SHIPPED",
  "carrier": "FedEx",
  "serviceLevel": "2-Day",
  "shippedAt": "2026-01-21T10:30:00"
}
```

### Valid Status Values
- `CREATED`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`
- `FAILED`
- `UNKNOWN`

### DateTime Format
- Use ISO 8601 format: `YYYY-MM-DDTHH:mm:ss`
- Example: `2026-01-21T10:30:00`

### Response (201 Created)
```json
{
  "fulfillmentId": "880e8400-e29b-41d4-a716-446655440003",
  "orderId": "770e8400-e29b-41d4-a716-446655440002",
  "externalFulfillmentId": "SHIP-001",
  "status": "SHIPPED",
  "carrier": "FedEx",
  "serviceLevel": "2-Day",
  "shippedAt": "2026-01-21T10:30:00",
  "createdAt": "2026-01-21T11:15:00Z",
  "updatedAt": "2026-01-21T11:15:00Z"
}
```

---

## 5. Common Errors and Solutions

### Error: 500 Internal Server Error

#### Possible Causes:

1. **Database Connection Issue**
   - Check if MySQL is running on `localhost:3306`
   - Check if database `logistics_platform` exists
   - Verify username: `root`, password: `root123`

2. **Missing Order ID**
   - When creating a fulfillment, ensure the `{orderId}` in the URL exists
   - Create an order first, then use its `orderId` to create fulfillments

3. **Invalid UUID Format**
   - UUIDs must be in format: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
   - Example: `550e8400-e29b-41d4-a716-446655440000`

4. **Invalid Enum Values**
   - Ensure status, financialStatus values match the allowed values listed above
   - Enum values are case-sensitive (use UPPERCASE)

5. **Foreign Key Constraint**
   - The `orgId` and `websiteId` must exist in the database
   - You may need to insert these first

### Error: 400 Bad Request

#### Possible Causes:

1. **Missing Required Fields**
   - For Order: `orgId`, `websiteId`, `externalOrderId` are required
   - For Fulfillment: `externalFulfillmentId` is required

2. **Invalid JSON Format**
   - Ensure proper JSON syntax (commas, quotes, brackets)
   - Use a JSON validator to check your payload

3. **Field Length Exceeded**
   - `externalOrderId`: max 128 characters
   - `externalFulfillmentId`: max 128 characters
   - `carrier`: max 64 characters
   - `serviceLevel`: max 64 characters
   - `currency`: exactly 3 characters

4. **Invalid Email Format**
   - Ensure `customerEmail` is a valid email address

---

## Step-by-Step Workflow for Creating a Fulfillment

### Step 1: Start Your Spring Boot Application
Make sure your application is running on port 8080.

### Step 2: Test Health Endpoint
```
GET http://localhost:8080/api/health
```
This should return a 200 OK response.

### Step 3: Create an Order
```
POST http://localhost:8080/api/orders
Content-Type: application/json

{
  "orgId": "550e8400-e29b-41d4-a716-446655440000",
  "websiteId": "660e8400-e29b-41d4-a716-446655440001",
  "externalOrderId": "SHOPIFY-12345",
  "externalOrderNumber": "ORD-2024-001",
  "status": "CREATED",
  "financialStatus": "PAID",
  "fulfillmentStatus": "UNFULFILLED",
  "customerEmail": "customer@example.com",
  "orderTotal": 129.99,
  "currency": "USD"
}
```

### Step 4: Copy the Order ID from Response
From the response, copy the `orderId` value (it will be a UUID).

### Step 5: Create a Fulfillment
```
POST http://localhost:8080/api/orders/{USE-THE-ORDER-ID-HERE}/fulfillments
Content-Type: application/json

{
  "externalFulfillmentId": "SHIP-001",
  "status": "SHIPPED",
  "carrier": "FedEx",
  "serviceLevel": "2-Day",
  "shippedAt": "2026-01-21T10:30:00"
}
```

---

## Quick Postman Setup

1. **Set Collection Variables** (optional but recommended):
   - `baseUrl`: `http://localhost:8080/api`
   - `tenantId`: `550e8400-e29b-41d4-a716-446655440000`
   - `storeId`: `660e8400-e29b-41d4-a716-446655440001`
   - `orderId`: (will be filled after creating an order)

2. **Import the Postman Collection**:
   - File: `postman/Fenix_Commerce_API.postman_collection.json`
   - This has all pre-configured requests

3. **After Creating an Order**:
   - Copy the `orderId` from the response
   - Set it as the collection variable `orderId`
   - Now you can use the fulfillment endpoints

---

## Troubleshooting Checklist

- [ ] Is the Spring Boot application running?
- [ ] Is MySQL running and accessible?
- [ ] Does the database `logistics_platform` exist?
- [ ] Have you created the tables (run the SQL scripts)?
- [ ] Are you using the correct base URL: `http://localhost:8080/api`?
- [ ] Are you including the `Content-Type: application/json` header?
- [ ] Is your JSON properly formatted?
- [ ] For fulfillments: Have you created an order first?
- [ ] Are you using valid UUIDs in the correct format?
- [ ] Do the `orgId` and `websiteId` exist in your database?

---

## Additional Resources

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/api/swagger-ui.html
```

### API Docs (OpenAPI/Swagger JSON)
```
http://localhost:8080/api/v3/api-docs
```
