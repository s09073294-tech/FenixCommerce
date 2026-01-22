# Quick Start Guide - Fenix Commerce Platform

Get up and running with the Fenix Commerce Platform in minutes!

## 📋 Prerequisites Checklist

Before you begin, ensure you have:

- [ ] **JDK 17** or higher installed
  ```bash
  java -version
  ```

- [ ] **Maven 3.8+** installed
  ```bash
  mvn -version
  ```

- [ ] **MySQL 8.0+** installed and running
  ```bash
  mysql --version
  ```

- [ ] **Git** installed (optional, for cloning)

## 🚀 5-Minute Setup

### Step 1: Database Setup (2 minutes)

#### 1.1 Create Database
```bash
mysql -u root -p
```

```sql
CREATE DATABASE logistics_platform 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_0900_ai_ci;

-- Verify
SHOW DATABASES LIKE 'logistics_platform';

-- Exit MySQL
EXIT;
```

#### 1.2 Run Schema Script
```bash
mysql -u root -p logistics_platform < database/schema.sql
```

#### 1.3 (Optional) Load Sample Data
```bash
mysql -u root -p logistics_platform < database/sample_data.sql
```

### Step 2: Configure Application (1 minute)

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/logistics_platform
    username: YOUR_MYSQL_USERNAME    # Change this
    password: YOUR_MYSQL_PASSWORD    # Change this
```

### Step 3: Build Project (1 minute)

```bash
mvn clean install
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

### Step 4: Run Application (1 minute)

```bash
mvn spring-boot:run
```

Wait for:
```
Started FenixCommerceApplication in X.XXX seconds
```

## ✅ Verify Installation

### Health Check

Open your browser or use curl:

```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{
  "status": "UP",
  "timestamp": "2024-01-20T...",
  "application": "Fenix Commerce Platform",
  "version": "1.0.0"
}
```

### Swagger UI

Open in browser:
```
http://localhost:8080/api/swagger-ui.html
```

You should see the interactive API documentation!

## 📝 Your First API Call

### 1. Create an Order

First, you need valid UUIDs for tenant and store. If you loaded sample data, use:
- Tenant ID: `550e8400-e29b-41d4-a716-446655440000` (Acme Corporation)
- Store ID: `650e8400-e29b-41d4-a716-446655440000` (Acme Shopify Store)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orgId": "550e8400-e29b-41d4-a716-446655440000",
    "websiteId": "650e8400-e29b-41d4-a716-446655440000",
    "externalOrderId": "TEST-ORDER-001",
    "externalOrderNumber": "ORD-TEST-001",
    "status": "CREATED",
    "financialStatus": "PAID",
    "fulfillmentStatus": "UNFULFILLED",
    "customerEmail": "test@example.com",
    "orderTotal": 99.99,
    "currency": "USD"
  }'
```

### 2. Get the Order

Copy the `id` from the response above, then:

```bash
curl http://localhost:8080/api/orders/{ORDER_ID}
```

### 3. Search Orders

```bash
curl "http://localhost:8080/api/orders?orgId=550e8400-e29b-41d4-a716-446655440000&page=0&size=10"
```

### 4. Create a Fulfillment

Using the order ID from step 1:

```bash
curl -X POST http://localhost:8080/api/orders/{ORDER_ID}/fulfillments \
  -H "Content-Type: application/json" \
  -d '{
    "externalFulfillmentId": "SHIP-001",
    "status": "SHIPPED",
    "carrier": "FedEx",
    "serviceLevel": "2-Day"
  }'
```

## 🧪 Using Postman

### Import Collection

1. Open Postman
2. Click **Import**
3. Select `postman/Fenix_Commerce_API.postman_collection.json`
4. Collection is ready to use!

### Set Variables

In Postman, set these collection variables:
- `baseUrl`: `http://localhost:8080/api`
- `tenantId`: `550e8400-e29b-41d4-a716-446655440000`
- `storeId`: `650e8400-e29b-41d4-a716-446655440000`

Now you can run all requests from the collection!

## 📊 Database Access

### Using MySQL Command Line

```bash
mysql -u root -p logistics_platform
```

### Useful Queries

#### View all tenants:
```sql
SELECT 
  BIN_TO_UUID(tenant_id, 1) as id,
  tenant_name,
  status
FROM tenant;
```

#### View all orders:
```sql
SELECT 
  BIN_TO_UUID(o.order_id, 1) as order_id,
  t.tenant_name,
  s.store_name,
  o.external_order_id,
  o.order_status,
  o.order_total_amount
FROM orders o
JOIN tenant t ON o.tenant_id = t.tenant_id
JOIN store s ON o.store_id = s.store_id
ORDER BY o.ingested_at DESC
LIMIT 10;
```

#### View orders with fulfillments:
```sql
SELECT 
  BIN_TO_UUID(o.order_id, 1) as order_id,
  o.external_order_id,
  COUNT(f.fulfillment_id) as fulfillment_count,
  GROUP_CONCAT(f.fulfillment_status) as statuses
FROM orders o
LEFT JOIN fulfillments f ON o.order_id = f.order_id
GROUP BY o.order_id, o.external_order_id;
```

## 🎯 Common Tasks

### Stop the Application

Press `Ctrl+C` in the terminal where the app is running

### Rebuild After Code Changes

```bash
mvn clean install
mvn spring-boot:run
```

### Run Tests

```bash
mvn test
```

### Package as JAR

```bash
mvn clean package
java -jar target/fenix-commerce-1.0.0.jar
```

## 🔧 Troubleshooting

### Port 8080 Already in Use

Change port in `application.yml`:
```yaml
server:
  port: 8081  # or any available port
```

### Database Connection Failed

1. Check MySQL is running:
   ```bash
   mysql -u root -p
   ```

2. Verify credentials in `application.yml`

3. Check database exists:
   ```sql
   SHOW DATABASES LIKE 'logistics_platform';
   ```

### Build Errors

1. Clean Maven cache:
   ```bash
   mvn clean
   ```

2. Delete Maven repository and rebuild:
   ```bash
   rm -rf ~/.m2/repository
   mvn clean install
   ```

### Application Won't Start

1. Check JDK version:
   ```bash
   java -version
   # Should be 17 or higher
   ```

2. Check logs in console for specific errors

3. Enable debug logging in `application.yml`:
   ```yaml
   logging:
     level:
       root: DEBUG
   ```

## 📚 Next Steps

1. **Explore the API**: Use Swagger UI to test all endpoints
2. **Read the Documentation**: Check `README.md` and `PROJECT_SUMMARY.md`
3. **Run Tests**: Execute `mvn test` to see the test suite
4. **Customize**: Add your own business logic and entities
5. **Deploy**: Package as JAR and deploy to your server

## 🆘 Getting Help

- **API Documentation**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api/v3/api-docs
- **Sample Data**: `database/sample_data.sql`
- **Postman Collection**: `postman/Fenix_Commerce_API.postman_collection.json`

## 🎉 Success!

If you've made it this far, congratulations! Your Fenix Commerce Platform is up and running. Start building your eCommerce order management workflow!

---

**Happy Coding!** 🚀
