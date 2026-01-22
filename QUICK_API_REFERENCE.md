# Quick API Reference - Fenix Commerce

## Base URL
```
http://localhost:8080/api
```

---

## 1️⃣ CREATE ORDER

### URL
```
POST http://localhost:8080/api/orders
```

### Headers
```
Content-Type: application/json
```

### JSON Body
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

**Valid Status Values:**
- status: `CREATED`, `PROCESSING`, `CLOSED`, `CANCELLED`
- financialStatus: `PENDING`, `PAID`, `PARTIALLY_PAID`, `REFUNDED`, `PARTIALLY_REFUNDED`
- fulfillmentStatus: `UNFULFILLED`, `PARTIALLY_FULFILLED`, `FULFILLED`

---

## 2️⃣ CREATE FULFILLMENT

### URL
```
POST http://localhost:8080/api/orders/{orderId}/fulfillments
```

**⚠️ Replace `{orderId}` with actual UUID from Create Order response**

### Example
```
POST http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002/fulfillments
```

### Headers
```
Content-Type: application/json
```

### JSON Body (Minimal)
```json
{
  "externalFulfillmentId": "SHIP-001"
}
```

### JSON Body (Complete)
```json
{
  "externalFulfillmentId": "SHIP-001",
  "status": "SHIPPED",
  "carrier": "FedEx",
  "serviceLevel": "2-Day",
  "shippedAt": "2026-01-21T10:30:00"
}
```

**Valid Status Values:**
- `CREATED`, `SHIPPED`, `DELIVERED`, `CANCELLED`, `FAILED`, `UNKNOWN`

**DateTime Format:**
- ISO 8601: `YYYY-MM-DDTHH:mm:ss`
- Example: `2026-01-21T10:30:00`

---

## 3️⃣ GET ORDER BY ID

### URL
```
GET http://localhost:8080/api/orders/{orderId}
```

### Example
```
GET http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002
```

---

## 4️⃣ SEARCH ORDERS

### URL
```
GET http://localhost:8080/api/orders?orgId={orgId}&page=0&size=10
```

### Example
```
GET http://localhost:8080/api/orders?orgId=550e8400-e29b-41d4-a716-446655440000&page=0&size=10&sort=orderUpdatedAt,desc
```

### Optional Query Parameters
- `websiteId` - Filter by website
- `status` - Filter by order status
- `financialStatus` - Filter by financial status
- `fulfillmentStatus` - Filter by fulfillment status
- `from` - Filter by date from (ISO 8601)
- `to` - Filter by date to (ISO 8601)
- `page` - Page number (default: 0)
- `size` - Page size (default: 50)
- `sort` - Sort field and direction (e.g., `orderUpdatedAt,desc`)

---

## 5️⃣ UPDATE ORDER (PARTIAL)

### URL
```
PATCH http://localhost:8080/api/orders/{orderId}
```

### Example
```
PATCH http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002
```

### Headers
```
Content-Type: application/json
```

### JSON Body
```json
{
  "status": "CLOSED",
  "fulfillmentStatus": "FULFILLED"
}
```

---

## 6️⃣ UPDATE FULFILLMENT (PARTIAL)

### URL
```
PATCH http://localhost:8080/api/orders/{orderId}/fulfillments/{fulfillmentId}
```

### Example
```
PATCH http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002/fulfillments/880e8400-e29b-41d4-a716-446655440003
```

### Headers
```
Content-Type: application/json
```

### JSON Body
```json
{
  "status": "DELIVERED",
  "deliveredAt": "2026-01-23T14:20:00"
}
```

---

## 7️⃣ LIST FULFILLMENTS FOR AN ORDER

### URL
```
GET http://localhost:8080/api/orders/{orderId}/fulfillments?page=0&size=10
```

### Example
```
GET http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002/fulfillments?page=0&size=10
```

### Optional Query Parameters
- `status` - Filter by fulfillment status
- `carrier` - Filter by carrier name
- `from` - Filter by date from (ISO 8601)
- `to` - Filter by date to (ISO 8601)
- `page` - Page number (default: 0)
- `size` - Page size (default: 50)

---

## 8️⃣ DELETE ORDER

### URL
```
DELETE http://localhost:8080/api/orders/{orderId}
```

### Example
```
DELETE http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002
```

**Response:** 204 No Content

---

## 9️⃣ DELETE FULFILLMENT

### URL
```
DELETE http://localhost:8080/api/orders/{orderId}/fulfillments/{fulfillmentId}
```

### Example
```
DELETE http://localhost:8080/api/orders/770e8400-e29b-41d4-a716-446655440002/fulfillments/880e8400-e29b-41d4-a716-446655440003
```

**Response:** 204 No Content

---

## 🔟 HEALTH CHECK

### URL
```
GET http://localhost:8080/api/health
```

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2026-01-21T11:10:00Z",
  "application": "Fenix Commerce Platform",
  "version": "1.0.0"
}
```

---

## 📝 Important Notes

1. **Base URL includes `/api`** - Don't forget it!
   - ✅ `http://localhost:8080/api/orders`
   - ❌ `http://localhost:8080/orders`

2. **UUIDs must have dashes**
   - ✅ `550e8400-e29b-41d4-a716-446655440000`
   - ❌ `550e8400e29b41d4a716446655440000`

3. **DateTime format is ISO 8601**
   - ✅ `2026-01-21T10:30:00`
   - ❌ `2026-01-21 10:30:00`

4. **Enum values are case-sensitive (UPPERCASE)**
   - ✅ `"status": "CREATED"`
   - ❌ `"status": "created"`

5. **Currency must be exactly 3 uppercase letters**
   - ✅ `"currency": "USD"`
   - ❌ `"currency": "usd"` or `"currency": "US"`

6. **Always include Content-Type header for POST/PUT/PATCH**
   - `Content-Type: application/json`

7. **Create order first, then create fulfillments**
   - Orders can exist without fulfillments
   - Fulfillments cannot exist without an order

---

## 🚨 Common Errors

### 500 Internal Server Error
- **Cause:** Database connection issue, missing foreign keys, or validation error
- **Solution:** Check Spring Boot console for detailed error message

### 404 Not Found
- **Cause:** Wrong URL or resource doesn't exist
- **Solution:** Verify the base URL includes `/api` and the UUID exists

### 400 Bad Request
- **Cause:** Invalid JSON, missing required fields, or invalid values
- **Solution:** Validate JSON syntax and check required fields

### 405 Method Not Allowed
- **Cause:** Wrong HTTP method
- **Solution:** Check if you're using POST/GET/PATCH/DELETE correctly

---

## 🔗 Additional Resources

- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **API Docs:** http://localhost:8080/api/v3/api-docs
- **Postman Collection:** `postman/Fenix_Commerce_API.postman_collection.json`
- **Detailed Troubleshooting:** See `POSTMAN_TROUBLESHOOTING.md`
- **Full API Examples:** See `API_EXAMPLES.md`
