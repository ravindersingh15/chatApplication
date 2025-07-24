# --- Stage 1: Build the application with Maven ---
# Start from a clean Java 11 runtime environment
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the pre-built JAR file from your local 'target' directory into the image
COPY target/sherlock-1.0-SNAPSHOT.jar /app/app.jar

# Copy the configuration template into the image
COPY config.yml .

# Expose the application and admin ports
EXPOSE 8080
EXPOSE 8081

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar", "server", "config.yml"]
