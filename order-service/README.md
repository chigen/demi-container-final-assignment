# Order Service

This is the Order Service component of the order processing system. It handles order creation, retrieval, and coordinates with other services like Inventory Service and Promotion Service.

## Features

- Create new orders
- Retrieve order details by order ID
- Retrieve all orders for a specific user
- Integration with Inventory Service for stock reservation
- ~~Integration with Promotion Service for discount application~~ (Not implemented yet)

## API Endpoints

### Create Order
- **POST** `/order`
- **Body:**
  ```json
  {
    "userId": 1,
    "itemId": "ITEM001",
    "quantity": 2
  }
  ```
- **Response:**
  ```json
  {
    "orderId": 1,
    "userId": 1,
    "itemId": "ITEM001",
    "quantity": 2,
    "totalPrice": 20.00,
    "createdAt": "2024-01-01T10:00:00",
    "status": "CREATED"
  }
  ```

### Get Order by ID
- **GET** `/order/{orderId}`
- **Response:** Order details or 404 if not found

### Get Orders by User ID
- **GET** `/order/user/{userId}`
- **Response:** List of orders for the specified user

## Configuration

The service runs on port 8080 and uses H2 in-memory database by default.

### Environment Variables
- `inventory.service.url`: URL for the Inventory Service (default: http://localhost:8081)
- ~~`promotion.service.url`: URL for the Promotion Service~~ (Not implemented yet)

## Dependencies

- Spring Boot 3.x
- Spring Cloud OpenFeign for service communication
- H2 Database for data storage
- Spring Boot Actuator for health checks
- Lombok for reducing boilerplate code

## Running the Service

1. Build the project:
   ```bash
   mvn clean package
   ```

2. Run the application:
   ```bash
   java -jar target/order-service-1.0-SNAPSHOT.jar
   ```

3. Access the H2 console at: http://localhost:8080/h2-console

## Health Check

- Health endpoint: http://localhost:8080/actuator/health
- Info endpoint: http://localhost:8080/actuator/info

## Service Communication

- **Inventory Service**: Running on port 8081
- **Promotion Service**: Not implemented yet (commented out in code)
