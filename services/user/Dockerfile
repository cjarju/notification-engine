# ---------------------
# Base
# ---------------------
FROM maven:3.9-eclipse-temurin-21 AS base

WORKDIR /workspace

# Copy the Maven wrapper and the parent POM
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Copy the child POM so Maven can understand the reactor
COPY services/user/pom.xml services/user/pom.xml

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# ---------------------
# Development
# ---------------------
FROM base AS dev

# Don't copy source for dev when using bind mounts

# Run the service in dev mode
CMD ["./mvnw", "-f", "services/user/pom.xml", "spring-boot:run"]

# ---------------------
# Build
# ---------------------
FROM base AS build

# Copy source
COPY services/user services/user

# Build the service and its dependencies
RUN ./mvnw -pl services/user -am clean package -DskipTests

# ---------------------
# Production
# ---------------------
FROM eclipse-temurin:21-jre AS prod

WORKDIR /app

COPY --from=build /workspace/services/user/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
