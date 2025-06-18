#!/bin/bash

echo "Building Inventory Service..."

# Clean and compile
mvn clean compile -pl inventory-service

# Run tests
mvn test -pl inventory-service

# Package the application
mvn package -pl inventory-service -DskipTests

echo "Inventory Service build completed!"
echo "JAR file location: inventory-service/target/" 
