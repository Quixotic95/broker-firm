# Broker Firm API

This project provides a backend service for a brokerage firm to manage stock orders and customer assets. The system enables employees to create, list, and cancel stock orders securely with role-based access control.

---

## 📌 Case Description

This backend API simulates a simple stock market brokerage system. The firm can manage customers and their stock orders (BUY/SELL) and track available balances per asset. Each order has a status: `PENDING`, `MATCHED`, or `CANCELED`.

### Requirements
- **Create Order:** Allows creating new orders with asset, side, size, and price.
- **Cancel Order:** Cancels an order that is still in `PENDING` status.
- **Match Order:** Admin can manually match an order.
- **List Orders:** Filters by customer, status, date.
- **List Assets:** Returns assets per customer.
- Orders always use `TRY` asset to buy/sell other assets.

> While creating or canceling an order, customer’s `TRY` or asset balance is updated accordingly.

All information is stored in an in-memory H2 database:
- **Asset**: customerId, assetName, size, usableSize
- **Order**: customerId, assetName, side, size, price, status, createDate

> No separate table for TRY — it is treated as an asset like others.

### Implementation Notes
- Spring Boot + H2 database
- RESTful API with proper layering (Controller, Service, Repository, Facade)
- Production-ready design with exception handling, validations, and AOP-based authorization
- Swagger for documentation, Postman preferred for testing
- Bonus 1: Customers can login and access only their data.
- Bonus 2: Admin can match orders manually and update balances accordingly.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the App
```bash
./mvnw spring-boot:run
```

The app will be available at: `http://localhost:8080`

---

## Authentication

- Basic Authentication is used.
- Default users are initialized at startup:

| Username   | Password   | Role      |
|------------|------------|-----------|
| `admin`    | `adminpass`| `ADMIN`   |
| `john_doe` | `password1`| `CUSTOMER`|
| `alice`    | `password2`| `CUSTOMER`|
| `bob`      | `password3`| `CUSTOMER`|

> To test with different users, open Swagger UI or Postman and enter the respective credentials.

---

## Swagger UI

> Swagger is enabled primarily for documentation purposes.

- URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

> ⚠️⚠️⚠️ Due to Basic Auth usage, browsers may cache credentials. Use **incognito/private window** to test with another user. ⚠️⚠️⚠️

---

## H2 Console

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:testdb`
- **Username**: `ing`
- **Password**: `hub`

---

## Testing

- Core service classes are fully unit tested.
- Tests use Mockito + JUnit 5.
- Coverage includes business logic, error handling, and AOP-based security.

```bash
./mvnw test
```

---

## Folder Structure

```bash
src
 ├── main
 │    ├── java
 │    │   └── com.github.quixotic95.brokerfirmchallenge
 │    │       ├── controller
 │    │       ├── dto
 │    │       ├── model
 │    │       ├── repository
 │    │       ├── service
 │    │       ├── facade
 │    │       ├── exception
 │    │       └── config / security
 │    └── resources
 │        └── application.properties
 └── test   
      ├─ ...
      └ ...
```

---

## 📬 Contact
For any issues or questions, feel free to reach out or open a pull request. ✌️

