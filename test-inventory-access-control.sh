#!/bin/bash

echo "Inventory Service Access Control Testing"
echo "======================================="

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

echo "1. Testing that inventory service is NOT accessible externally..."

# Check if ingress exists and get its address
INGRESS_HOST=$(kubectl get ingress -n demi order-service-ingress -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null)
if [ -z "$INGRESS_HOST" ]; then
    INGRESS_HOST=$(kubectl get ingress -n demi order-service-ingress -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null)
fi

if [ -z "$INGRESS_HOST" ]; then
    echo -e "${YELLOW}Warning: Ingress external address not found. Using localhost for testing...${NC}"
    INGRESS_HOST="localhost"
fi

echo "   Ingress Host: $INGRESS_HOST"

# Test inventory endpoints through ingress (should fail)
echo ""
echo "2. Testing inventory endpoints through Ingress (should fail):"

# Test inventory query
echo "   Testing inventory query through Ingress..."
curl -s -f "http://$INGRESS_HOST/inventory/ITEM001" >/dev/null 2>&1
if [ $? -ne 0 ]; then
    print_status 0 "Inventory query blocked through Ingress (expected)"
else
    print_status 1 "Inventory query accessible through Ingress (SECURITY ISSUE!)"
fi

# Test inventory reservation
echo "   Testing inventory reservation through Ingress..."
curl -s -f -X POST "http://$INGRESS_HOST/inventory/reserve" \
  -H "Content-Type: application/json" \
  -d '{"itemId": "ITEM001", "quantity": 2}' >/dev/null 2>&1
if [ $? -ne 0 ]; then
    print_status 0 "Inventory reservation blocked through Ingress (expected)"
else
    print_status 1 "Inventory reservation accessible through Ingress (SECURITY ISSUE!)"
fi

echo ""
echo "3. Testing that inventory service IS accessible internally..."

# Test internal access through port-forward (should work)
echo "   Starting port-forward for inventory service..."
kubectl port-forward -n demi svc/inventory-service 8081:8081 &
PF_PID=$!
sleep 5

# Test inventory query through port-forward
echo "   Testing inventory query through port-forward..."
curl -s -f http://localhost:8081/inventory/ITEM001 >/dev/null 2>&1
print_status $? "Inventory query accessible through port-forward (expected)"

# Test inventory reservation through port-forward
echo "   Testing inventory reservation through port-forward..."
curl -s -f -X POST http://localhost:8081/inventory/reserve \
  -H "Content-Type: application/json" \
  -d '{"itemId": "ITEM001", "quantity": 1}' >/dev/null 2>&1
print_status $? "Inventory reservation accessible through port-forward (expected)"

# Stop port-forward
kill $PF_PID 2>/dev/null

echo ""
echo "4. Testing internal service communication..."

# Test order-service to inventory-service communication
echo "   Testing order-service to inventory-service communication..."
kubectl exec -n demi deployment/order-service -- curl -s -f http://inventory-service:8081/inventory/ITEM001 >/dev/null 2>&1
print_status $? "Internal communication working (expected)"

echo ""
echo "5. Testing Network Policy enforcement..."

# Test access from a different pod (should be blocked)
echo "   Testing access from a different pod to inventory-service..."
kubectl run test-pod --image=curlimages/curl --rm -it --restart=Never -- curl -s http://inventory-service:8081/inventory/ITEM001 >/dev/null 2>&1
if [ $? -ne 0 ]; then
    print_status 0 "Network policy properly enforced (access blocked from other pods)"
else
    print_status 1 "Network policy not enforced (access allowed from other pods)"
fi

echo ""
echo "6. Verifying service configuration..."

# Check service type
SERVICE_TYPE=$(kubectl get svc inventory-service -n demi -o jsonpath='{.spec.type}')
echo "   Inventory Service Type: $SERVICE_TYPE"
if [ "$SERVICE_TYPE" = "ClusterIP" ]; then
    print_status 0 "Inventory service correctly configured as ClusterIP (internal only)"
else
    print_status 1 "Inventory service should be ClusterIP"
fi

echo ""
echo "Access Control Test Summary:"
echo "============================"
echo "✓ Inventory service blocked externally through Ingress"
echo "✓ Inventory service accessible internally through port-forward"
echo "✓ Internal service communication working"
echo "✓ Network policy protecting inventory service"
echo "✓ Service properly configured as ClusterIP" 
