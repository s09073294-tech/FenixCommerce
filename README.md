# Fenix Commerce Platform

A production-ready, multi-tenant eCommerce Order Management System built with **Java 17** and **Spring Boot 3.2**.

## 🚀 Features

- **Multi-Tenant Architecture**: Supports multiple organizations, each with their own eCommerce websites
- **Order Management**: Complete CRUD operations for orders with status tracking
- **Fulfillment Management**: Track fulfillments, shipping, and delivery information
- **Tracking System**: Monitor shipment tracking with carrier integration support
- **RESTful APIs**: Clean, well-documented REST APIs following OpenAPI 3.0 specification
- **Advanced Search**: Pagination, filtering, and date range queries
- **Data Validation**: Comprehensive input validation with detailed error messages
- **Exception Handling**: Centralized error handling with meaningful responses

## 🏗️ Architecture

The application follows a clean, layered architecture:

```
┌─────────────────┐
│   Controllers   │  ← REST API Layer
├─────────────────┤
│    Services     │  ← Business Logic Layer
├─────────────────┤
│  Repositories   │  ← Data Access Layer
├─────────────────┤
│    Entities     │  ← Domain Models
└─────────────────┘
```

### Technology Stack

- **Java 17**: Modern Java features and performance improvements
- **Spring Boot 3.2.1**: Latest Spring Boot for enterprise applications
- **Spring Data JPA**: Simplified data access with Hibernate
- **MySQL 8.0+**: Reliable relational database
- **Maven**: Dependency management and build tool
- **Lombok**: Reduces boilerplate code
- **Springdoc OpenAPI**: Auto-generated API documentation
- **Bean Validation**: Input validation using Jakarta Validation

## 📋 Prerequisites

- **JDK 17** or higher
- **Maven 3.8+**
- **MySQL 8.0+**
- **Git**

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd FenixCommerce
```

### 2. Set Up MySQL Database

Create the database:

```sql
CREATE DATABASE logistics_platform
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

Update database credentials in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/logistics_platform
    username: your_username
    password: your_password
```

### 3. Run Database Schema

Execute the provided SQL schema file to create all tables:

```bash
mysql -u your_username -p logistics_platform < database/schema.sql
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

## 🔑 Key Endpoints

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/orders` | Create or upsert an order |
| GET | `/orders/{orderId}` | Get order by ID |
| GET | `/orders` | Search orders with filters |
| GET | `/orders/search` | Search by external IDs |
| PUT | `/orders/{orderId}` | Update order (full) |
| PATCH | `/orders/{orderId}` | Update order (partial) |
| DELETE | `/orders/{orderId}` | Delete order |

### Fulfillments

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/orders/{orderId}/fulfillments` | Create fulfillment |
| GET | `/orders/{orderId}/fulfillments/{id}` | Get fulfillment by ID |
| GET | `/orders/{orderId}/fulfillments` | List fulfillments with filters |
| GET | `/orders/{orderId}/fulfillments/search` | Search by external ID |
| PUT | `/orders/{orderId}/fulfillments/{id}` | Update fulfillment (full) |
| PATCH | `/orders/{orderId}/fulfillments/{id}` | Update fulfillment (partial) |
| DELETE | `/orders/{orderId}/fulfillments/{id}` | Delete fulfillment |

## 📊 Database Schema

The system uses the following core entities:

- **tenant**: Organizations using the platform
- **store**: eCommerce websites belonging to tenants
- **orders**: Customer orders from stores
- **order_items**: Line items within orders
- **fulfillments**: Order fulfillment records
- **tracking**: Shipment tracking information
- **tracking_events**: Tracking event history

All primary keys use UUIDs stored as `BINARY(16)` for optimal performance.

## 🔒 Multi-Tenant Data Isolation

The system enforces tenant-level data isolation:

- All queries automatically filter by `tenant_id`
- Relationships validate cross-tenant access
- Natural keys include `tenant_id` for uniqueness

## 🎯 Example API Usage

### Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orgId": "550e8400-e29b-41d4-a716-446655440000",
    "websiteId": "550e8400-e29b-41d4-a716-446655440001",
    "externalOrderId": "SHOPIFY-12345",
    "externalOrderNumber": "ORD-2024-001",
    "status": "CREATED",
    "financialStatus": "PAID",
    "fulfillmentStatus": "UNFULFILLED",
    "customerEmail": "customer@example.com",
    "orderTotal": 129.99,
    "currency": "USD"
  }'
```

### Search Orders

```bash
curl "http://localhost:8080/api/orders?orgId=550e8400-e29b-41d4-a716-446655440000&page=0&size=10&sort=orderUpdatedAt,desc"
```

### Create a Fulfillment

```bash
curl -X POST http://localhost:8080/api/orders/{orderId}/fulfillments \
  -H "Content-Type: application/json" \
  -d '{
    "externalFulfillmentId": "SHIP-001",
    "status": "SHIPPED",
    "carrier": "FedEx",
    "serviceLevel": "2-Day"
  }'
```

## ✅ Features Implemented

### Core Requirements
- ✅ Multi-tenant support with proper data isolation
- ✅ REST-based APIs with request/response contracts
- ✅ Clean separation of concerns (Controller → Service → Repository)
- ✅ Order CRUD operations
- ✅ Fulfillment CRUD operations
- ✅ Scalable database design with proper indexes
- ✅ Error handling and validation

### Bonus Features
- ✅ Pagination and filtering
- ✅ Date range queries
- ✅ Search by external IDs
- ✅ OpenAPI 3.0 documentation
- ✅ Input validation with detailed error messages
- ✅ Comprehensive exception handling

## 🧪 Testing

Run tests:

```bash
mvn test
```

## 📝 Project Structure

```
FenixCommerce/
├── src/
│   ├── main/
│   │   ├── java/com/fenix/commerce/
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── service/          # Business Logic
│   │   │   ├── repository/       # Data Access
│   │   │   ├── entity/           # Domain Models
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Custom Exceptions
│   │   │   └── FenixCommerceApplication.java
│   │   └── resources/
│   │       └── application.yml   # Configuration
│   └── test/                     # Test Cases
├── database/                     # SQL Scripts
├── pom.xml                       # Maven Dependencies
└── README.md
```

## 🎓 Design Decisions

1. **UUID as Primary Keys**: Better for distributed systems and prevents ID guessing
2. **Enum for Status Fields**: Type-safe status management
3. **Lazy Loading**: Optimizes performance by loading related entities only when needed
4. **Pagination**: Prevents memory issues with large datasets
5. **DTO Pattern**: Separates API contracts from domain models
6. **Global Exception Handler**: Centralized error handling for consistent responses

## 🚀 Future Enhancements

- [ ] Authentication and Authorization (Spring Security)
- [ ] Event-based processing (Spring Events / Kafka)
- [ ] Caching layer (Redis)
- [ ] Unit and integration tests
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Rate limiting
- [ ] Audit logging

## 📄 License

This project is part of the Fenix Commerce technical evaluation.

## 👥 Contact

For questions or support, please contact the development team.
