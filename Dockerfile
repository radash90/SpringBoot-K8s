# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/spring-boot*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

