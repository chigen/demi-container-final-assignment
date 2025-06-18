#!/bin/bash

# Update deployment files with current Git SHA
set -e

GIT_SHA=$(git rev-parse HEAD)
echo "Updating deployment files with SHA: $GIT_SHA"

# Update inventory-service deployment
sed -i.bak "s|image:.*inventory-service.*|image: ghcr.io/chigen/demi-container-final-assignment/inventory-service:$GIT_SHA|" k8s/base/inventory-service-deployment.yaml

# Update order-service deployment
sed -i.bak "s|image:.*order-service.*|image: ghcr.io/chigen/demi-container-final-assignment/order-service:$GIT_SHA|" k8s/base/order-service-deployment.yaml

echo "Deployment files updated successfully!"
echo "SHA: $GIT_SHA"

# Clean up backup files
rm -f k8s/base/*.bak 
