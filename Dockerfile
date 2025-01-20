# Build Stage: Use Maven to build the project
FROM eclipse-temurin:17-jdk-jammy AS build

# Set working directory inside the container
WORKDIR /app

# Install Maven and Netcat
USER root
RUN apt-get update && apt-get install -y maven netcat && apt-get clean

# Copy the source code to the container
COPY . .

# Final Stage: Final image with OpenJDK 17
FROM eclipse-temurin:17-jdk-jammy

# Metadata about the author
LABEL authors="adrianogoulart"

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file generated in the build stage into the final image
COPY --from=build /app/target/echo-server-1.0-SNAPSHOT.jar /app/echo-server.jar

# Expose port 8081 for the application
EXPOSE 8081

# Set the command to run the application inside the container
ENTRYPOINT ["java", "-jar", "echo-server.jar"]
