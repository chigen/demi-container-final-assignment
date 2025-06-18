#!/bin/bash

# Deploy script for microservices
# Usage: ./deploy.sh [environment] [version]
# Example: ./deploy.sh dev latest
# Example: ./deploy.sh prod e5e98933a8209b3e768d9ff3c716f924b3004888

set -e

ENVIRONMENT=${1:-prod}
VERSION=${2:-latest}
REGISTRY="ghcr.io"
IMAGE_NAME="chigen/demi-container-final-assignment"

echo "Deploying to $ENVIRONMENT environment with version $VERSION"

# Validate environment
if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "prod" ]]; then
    echo "Error: Environment must be 'dev' or 'prod'"
    exit 1
fi

# Update image tags in kustomization
echo "Updating image tags..."
for service in inventory-service order-service; do
    yq eval ".images[] |= select(.name == \"$REGISTRY/$IMAGE_NAME/$service\") .newTag = \"$VERSION\"" -i k8s/overlays/$ENVIRONMENT/kustomization.yaml
done

# Apply k8s manifests
echo "Applying Kubernetes manifests..."
kubectl apply -k k8s/overlays/$ENVIRONMENT

echo "Deployment completed successfully!"
echo "Environment: $ENVIRONMENT"
echo "Version: $VERSION"

# Show deployment status
echo "Checking deployment status..."
kubectl get pods -n demi$([ "$ENVIRONMENT" = "dev" ] && echo "-dev" || echo "") -l app=inventory-service
kubectl get pods -n demi$([ "$ENVIRONMENT" = "dev" ] && echo "-dev" || echo "") -l app=order-service 
