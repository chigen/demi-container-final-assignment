FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the parent pom and the entire order-service directory
COPY pom.xml .
COPY order-service/ order-service/

# Download dependencies
RUN mvn dependency:go-offline -B

# Build the application
RUN mvn clean package -pl order-service -am -DskipTests

# Create runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/order-service/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 
