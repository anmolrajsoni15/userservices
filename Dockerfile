# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean package -DskipTests

# Stage 2: Use a distroless base image
FROM gcr.io/distroless/java17-debian11
WORKDIR /app
COPY --from=builder /app/target/*.jar userservice.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "userservice.jar"]
