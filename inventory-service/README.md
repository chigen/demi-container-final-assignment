# Inventory Service

This is the micro service in the order processing system. It handles the product inventory and product reservation features.

## Features

- Query product inventory information
- Reserve product inventory
- Inventory management (total inventory, reserved inventory, available inventory)
- **Use built-in H2 in-memory database** - automatically init sample data at eacg startup

## API endpoints

### 1. Query inventory
```
GET /inventory/{itemId}
```

**response example：**
```json
{
  "itemId": "ITEM001",
  "stock": 100,
  "reservedStock": 10,
  "availableStock": 90
}
```

### 2. reserve inventory
```
POST /inventory/reserve
```

**Resquest body：**
```json
{
  "itemId": "ITEM001",
  "quantity": 5
}
```

**response example:**
```json
{
  "success": true,
  "message": "Inventory reserved successfully",
  "itemId": "ITEM001",
  "reservedQuantity": 5,
  "remainingAvailableStock": 85
}
```

## Local set-up

### prerequisite
- Java 17
- Maven 3.6+

### Running step

1. **Compiling**
```bash
mvn clean compile
```

2. **Run application**
```bash
mvn spring-boot:run -pl inventory-service
```

application will be run at `http://localhost:8081` 

### Access H2 controller
```
http://localhost:8081/h2-console
```
- JDBC URL: `jdbc:h2:mem:inventory_db`
- Username: `sa`
- Password: 

## Kubernetes deployment

### Build Docker image
```bash
docker build -t inventory-service:latest .
```

### Deploy into Kubernetes
```bash
kubectl apply -f k8s/base/
```

### Check deployment status
```bash
kubectl get pods -n demi
kubectl get services -n demi
```

## Health check

```
GET /actuator/health
```

## log

log-level config：
- `com.example.inventoryservice`: DEBUG
- `org.springframework.web`: DEBUG
- `org.hibernate.SQL`: DEBUG

## sample data

Sample data is automatically inserted each time the service starts：
- ITEM001: 100 units
- ITEM002: 50 units  
- ITEM003: 200 units
- ITEM004: 75 units
- ITEM005: 150 units

## Configuration instructions

### Database initialization process
1. **Service startup** → H2 memory database initialization
2. **Table structure creation** → Execute `schema.sql`
3. **Sample data insertion** → Execute `data.sql`
4. **Service ready** → Can receive API requests

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Recreate the table every time it starts
  sql:
    init:
      mode: always  # Always exec the init script
```
