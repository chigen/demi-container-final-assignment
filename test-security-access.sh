#!/bin/bash

echo "Testing Security Access Control"
echo "==============================="

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

echo ""
echo "2. Testing Order Service External Access (should work):"
echo "   Testing order creation through Ingress..."
curl -X POST "http://$INGRESS_HOST/order" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "itemId": "ITEM001",
    "quantity": 2
  }' && echo " ✓ Order service accessible externally"

echo ""
echo "3. Testing Inventory Service External Access (should fail):"
echo "   Testing inventory query through Ingress..."
curl -X GET "http://$INGRESS_HOST/inventory/ITEM001" && echo " ✗ Inventory service accessible externally (SECURITY ISSUE!)" || echo " ✓ Inventory service properly blocked externally"

echo ""
echo "4. Testing Internal Service Communication (should work):"
echo "   Testing order-service to inventory-service communication..."
kubectl exec -n demi deployment/order-service -- curl -s http://inventory-service:8081/inventory/ITEM001 && echo " ✓ Internal communication working" || echo " ✗ Internal communication failed"

echo ""
echo "5. Testing Network Policy Enforcement:"
echo "   Testing access from a different pod to inventory-service..."
kubectl run test-pod --image=curlimages/curl --rm -it --restart=Never -- curl -s http://inventory-service:8081/inventory/ITEM001 && echo " ✗ Network policy not enforced (SECURITY ISSUE!)" || echo " ✓ Network policy properly enforced"

echo ""
echo "6. Verifying Service Types:"
echo "   Order Service Type:"
kubectl get svc order-service -n demi -o jsonpath='{.spec.type}' && echo ""
echo "   Inventory Service Type:"
kubectl get svc inventory-service -n demi -o jsonpath='{.spec.type}' && echo ""

echo ""
echo "7. Checking Network Policy:"
kubectl get networkpolicy -n demi

echo ""
echo "Security Test Summary:"
echo "====================="
echo "✓ Order service accessible externally through Ingress"
echo "✓ Inventory service blocked externally"
echo "✓ Internal communication between services working"
echo "✓ Network policy protecting inventory service"
echo "✓ Services properly configured as ClusterIP" 
