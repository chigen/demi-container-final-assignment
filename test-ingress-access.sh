#!/bin/bash

echo "Testing Ingress Access Control"
echo "=============================="

# Get the Ingress external IP/hostname
echo "1. Getting Ingress external address..."
INGRESS_HOST=$(kubectl get ingress -n demi order-service-ingress -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
if [ -z "$INGRESS_HOST" ]; then
    INGRESS_HOST=$(kubectl get ingress -n demi order-service-ingress -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
fi

if [ -z "$INGRESS_HOST" ]; then
    echo "Warning: Ingress external address not found. Using localhost for testing..."
    INGRESS_HOST="localhost"
fi

echo "Ingress Host: $INGRESS_HOST"

# Test order-service APIs (should be accessible)
echo ""
echo "2. Testing Order Service APIs (should be accessible):"
echo "   Testing health endpoint..."
curl -X GET "http://$INGRESS_HOST/actuator/health" || echo "Failed to access health endpoint"

echo ""
echo "   Testing order creation..."
curl -X POST "http://$INGRESS_HOST/order" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "itemId": "ITEM001",
    "quantity": 2
  }' || echo "Failed to create order"

echo ""
echo "   Testing order retrieval..."
curl -X GET "http://$INGRESS_HOST/order/1" || echo "Failed to retrieve order"

# Test inventory-service APIs (should NOT be accessible)
echo ""
echo "3. Testing Inventory Service APIs (should NOT be accessible):"
echo "   Testing inventory query..."
curl -X GET "http://$INGRESS_HOST/inventory/ITEM001" || echo "Inventory endpoint not accessible (expected)"

echo ""
echo "   Testing inventory reservation..."
curl -X POST "http://$INGRESS_HOST/inventory/reserve" \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": "ITEM001",
    "quantity": 2
  }' || echo "Inventory reserve endpoint not accessible (expected)"

echo ""
echo "4. Testing internal service communication:"
echo "   Checking if order-service can still communicate with inventory-service internally..."
kubectl exec -n demi deployment/order-service -- curl -s http://inventory-service:8081/inventory/ITEM001 || echo "Internal communication failed"

echo ""
echo "Test completed!" 
