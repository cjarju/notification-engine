# ---------------------
# Base
# ---------------------
FROM eclipse-temurin:21-jdk AS base

WORKDIR /workspace

# Copy the Maven wrapper and the parent POM
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Copy the child POMs so Maven can understand the reactor
COPY services/user/pom.xml services/user/pom.xml
COPY services/dispatcher/pom.xml services/dispatcher/pom.xml
COPY services/ingestion/pom.xml services/ingestion/pom.xml
COPY services/gateway/pom.xml services/gateway/pom.xml

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
CMD ["./mvnw", "-f", "services/ingestion/pom.xml", "spring-boot:run"]

# ---------------------
# Build
# ---------------------
FROM base AS build

# Copy source
COPY services/ingestion services/ingestion

# Build the service and its dependencies
RUN ./mvnw -pl services/ingestion -am clean package -DskipTests

# ---------------------
# Production
# ---------------------
FROM eclipse-temurin:21-jre AS prod

WORKDIR /app

COPY --from=build /workspace/services/ingestion/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
