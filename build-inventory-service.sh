#!/bin/bash

# Build and push services with Git SHA tags
set -e

# Configuration
REGISTRY="ghcr.io"
IMAGE_NAME="chigen/demi-container-final-assignment"
GIT_SHA=$(git rev-parse HEAD)

echo "Building and pushing services with SHA: $GIT_SHA"

# Build and push both services
for service in inventory-service order-service; do
    echo "Building $service..."
    
    # Build the image with SHA tag
    docker build -t $REGISTRY/$IMAGE_NAME/$service:$GIT_SHA -f $service/Dockerfile .
    
    # Also tag as latest for convenience
    docker tag $REGISTRY/$IMAGE_NAME/$service:$GIT_SHA $REGISTRY/$IMAGE_NAME/$service:latest
    
    echo "Pushing $service images..."
    
    # Push both SHA and latest tags
    docker push $REGISTRY/$IMAGE_NAME/$service:$GIT_SHA
    docker push $REGISTRY/$IMAGE_NAME/$service:latest
    
    echo "Successfully built and pushed $service"
done

echo "All services built and pushed successfully!"
echo "SHA: $GIT_SHA"
echo "Latest tags also pushed for convenience" 
