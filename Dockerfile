FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Package with dependencies included
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

# Set noninteractive to avoid prompts
ENV DEBIAN_FRONTEND=noninteractive

# Update package names for JavaFX dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    libgl1 \
    libgtk-3-0 \
    libgdk-pixbuf2.0-0 \
    libx11-6 \
    libxcb1 \
    libxtst6 \
    libxrender1 \
    xauth \
    x11-apps \
    mariadb-server \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Environment variables
ENV DISPLAY=:0

# Copy the built application and its dependencies
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# If maven creates a separate directory with dependencies, copy those as well
COPY --from=build /app/target/dependency/*.jar ./lib/

# Create database init script
RUN echo "CREATE DATABASE IF NOT EXISTS stms; \
CREATE USER IF NOT EXISTS 'stms_user'@'localhost' IDENTIFIED BY 'password'; \
GRANT ALL PRIVILEGES ON stms.* TO 'stms_user'@'localhost'; \
FLUSH PRIVILEGES;" > /app/db_init.sql

# Create a startup script with proper line breaks
RUN echo '#!/bin/bash\n\
service mariadb start\n\
sleep 5\n\
mysql -u root < /app/db_init.sql\n\
# Run with classpath including all dependencies\n\
java -cp app.jar:lib/* Main\n' > /app/start.sh && chmod +x /app/start.sh

# Set the entry point
ENTRYPOINT ["/bin/bash", "/app/start.sh"]