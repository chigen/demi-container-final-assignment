FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY inventory-service/pom.xml inventory-service/

# Download dependencies
RUN mvn dependency:go-offline -B

COPY inventory-service/src inventory-service/src

RUN mvn clean package -pl inventory-service -am -DskipTests

# Create runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/inventory-service/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 
