# Step 1: Use Java 17 as base image
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy the JAR file into container
COPY target/*.jar app.jar

# Step 4: Expose port 8080
EXPOSE 8080

# Step 5: Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]