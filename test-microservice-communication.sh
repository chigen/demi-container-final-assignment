#!/bin/bash

echo "Testing Microservice Communication"
echo "=================================="

# Start inventory service (if not already running)
echo "1. Starting Inventory Service..."
cd inventory-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081" &
INVENTORY_PID=$!
cd ..

# Wait for inventory service to start
echo "2. Waiting for Inventory Service to start..."
sleep 10

# Test inventory service directly
echo "3. Testing Inventory Service..."
curl -X GET http://localhost:8081/inventory/ITEM001
echo ""
echo ""

# Start order service (if not already running)
echo "4. Starting Order Service..."
cd order-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080" &
ORDER_PID=$!
cd ..

# Wait for order service to start
echo "5. Waiting for Order Service to start..."
sleep 10

# Test order creation
echo "6. Testing Order Creation..."
curl -X POST http://localhost:8080/order \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "itemId": "ITEM001",
    "quantity": 2
  }'
echo ""
echo ""

# Test order retrieval
echo "7. Testing Order Retrieval..."
curl -X GET http://localhost:8080/order/1
echo ""
echo ""

# Test inventory after order
echo "8. Testing Inventory After Order..."
curl -X GET http://localhost:8081/inventory/ITEM001
echo ""
echo ""

echo "Test completed!"
echo "To stop services, run: kill $INVENTORY_PID $ORDER_PID" 
