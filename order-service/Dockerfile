FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Maven wrapper and pom files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY order-service/pom.xml order-service/

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY order-service/src order-service/src

# Build the application
RUN ./mvnw clean package -pl order-service -am -DskipTests

# Create runtime image
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=0 /app/order-service/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 
