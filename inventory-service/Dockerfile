FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the inventory-service module files
COPY . .

# Download dependencies and build the application
RUN mvn clean compile dependency:copy-dependencies -DskipTests

# Create runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the compiled classes and dependencies
COPY --from=build /app/target/classes /app/classes
COPY --from=build /app/target/dependency /app/dependency

# Expose port
EXPOSE 8081

# Run the application using classpath
ENTRYPOINT ["java", "-cp", "/app/classes:/app/dependency/*", "com.example.inventoryservice.InventoryServiceApplication"] 
