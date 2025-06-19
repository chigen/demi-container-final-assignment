FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the inventory-service module files
COPY . .

# Download dependencies and build the application
RUN mvn clean package -DskipTests

# Create runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 
