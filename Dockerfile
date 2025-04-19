FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    libgdk-pixbuf2.0-0 \
    libgl1 \
    libgtk-3-0 \
    libx11-6 \
    libxcb1 \
    libxrender1 \
    libxtst6 \
    mariadb-server \
    openjfx \
    x11-apps \
    xauth \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*


# Environment variables
ENV DISPLAY=host.docker.internal:0.0

# Copy the built application and its dependencies
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/dependency/*.jar ./lib/

# Entry point optimized for external database connection
ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "main.Main"]