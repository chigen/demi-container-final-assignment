#!/bin/bash

echo "Order Service API Testing"
echo "========================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ $2${NC}"
    else
        echo -e "${RED}✗ $2${NC}"
    fi
}

# Function to test API endpoint
test_api() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo "   Testing $description..."
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$endpoint" -H "Content-Type: application/json" -d "$data")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        print_status 0 "$description (HTTP $http_code)"
        echo "     Response: $body" | head -c 100
        [ ${#body} -gt 100 ] && echo "..."
    else
        print_status 1 "$description (HTTP $http_code)"
        echo "     Response: $body"
    fi
    echo ""
}

# Check if pods are ready
echo "1. Checking if order service is ready..."
kubectl wait --for=condition=ready pod -l app=order-service -n demi --timeout=60s
if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Order service pods not ready${NC}"
    exit 1
fi
print_status 0 "Order service pods ready"

# Start port-forward
echo "2. Starting port-forward for order service..."
kubectl port-forward -n demi svc/order-service 8080:8080 &
PF_PID=$!
sleep 5

# Test health endpoint
echo "3. Testing health endpoint..."
test_api "GET" "http://localhost:8080/actuator/health" "" "Health check endpoint"

# Test order creation
echo "4. Testing order creation..."
ORDER_DATA='{"userId": 1, "itemId": "ITEM001", "quantity": 2}'
test_api "POST" "http://localhost:8080/order" "$ORDER_DATA" "Create order"

# Test order creation with different data
echo "5. Testing order creation with different items..."
ORDER_DATA2='{"userId": 2, "itemId": "ITEM002", "quantity": 1}'
test_api "POST" "http://localhost:8080/order" "$ORDER_DATA2" "Create order with different user and item"

# Test order creation with invalid data
echo "6. Testing order creation with invalid data..."
INVALID_DATA='{"userId": 1, "itemId": "ITEM001", "quantity": 0}'
test_api "POST" "http://localhost:8080/order" "$INVALID_DATA" "Create order with invalid quantity (should fail)"

# Test order retrieval
echo "7. Testing order retrieval..."
test_api "GET" "http://localhost:8080/order/1" "" "Get order by ID"

# Test order retrieval for non-existent order
echo "8. Testing order retrieval for non-existent order..."
test_api "GET" "http://localhost:8080/order/999" "" "Get non-existent order (should return 404)"

# Test get orders by user ID
echo "9. Testing get orders by user ID..."
test_api "GET" "http://localhost:8080/order/user/1" "" "Get orders by user ID"

# Test get orders by non-existent user ID
echo "10. Testing get orders by non-existent user ID..."
test_api "GET" "http://localhost:8080/order/user/999" "" "Get orders by non-existent user ID"

# Test invalid endpoints
echo "11. Testing invalid endpoints..."
test_api "GET" "http://localhost:8080/invalid" "" "Invalid endpoint (should return 404)"

# Test inventory endpoint (should not be accessible through order service)
echo "12. Testing inventory endpoint through order service..."
test_api "GET" "http://localhost:8080/inventory/ITEM001" "" "Inventory endpoint through order service (should return 404)"

# Stop port-forward
echo "13. Stopping port-forward..."
kill $PF_PID 2>/dev/null

echo ""
echo "API Test Summary:"
echo "================="
echo "✓ Health endpoint tested"
echo "✓ Order creation tested (valid and invalid data)"
echo "✓ Order retrieval tested (existing and non-existing orders)"
echo "✓ Get orders by user ID tested"
echo "✓ Invalid endpoints tested"
echo "✓ Security (inventory endpoint blocked) tested"

echo ""
echo "Note: All tests used kubectl port-forward to access the service directly."
echo "In production, these endpoints would be accessed through the Ingress controller." 
