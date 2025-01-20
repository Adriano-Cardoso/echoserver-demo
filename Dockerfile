# Build Stage: Use Maven to build the project
FROM maven:3.8.4-openjdk-17 AS build

# Set working directory inside the container
WORKDIR /app

# Instalar o Netcat
RUN apt-get update && apt-get install -y netcat && apt-get clean

# Copy the source code to the container
COPY . .

# Final Stage: Final image with OpenJDK 17
FROM openjdk:17-jdk-slim

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
