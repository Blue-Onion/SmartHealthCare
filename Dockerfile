# ============================================================
# Stage 1: Build the application with Maven
# ============================================================
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy the POM first to cache dependency downloads
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the fat JAR
COPY src ./src
RUN mvn clean package -B -DskipTests \
    && mvn dependency:copy-dependencies -DoutputDirectory=target/libs

# ============================================================
# Stage 2: Lightweight runtime image
# ============================================================
FROM eclipse-temurin:11-jre

# Install X11 libraries required by Java Swing / AWT
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libx11-6 \
        libxext6 \
        libxrender1 \
        libxtst6 \
        libxi6 \
        libfreetype6 \
        fontconfig \
        fonts-dejavu-core && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy compiled classes and dependencies from the builder stage
COPY --from=builder /app/target/classes ./classes
COPY --from=builder /app/target/libs ./libs

# Default environment variable so AWT can find a display
ENV DISPLAY=:0

# Run the main class
ENTRYPOINT ["java", "-cp", "classes:libs/*", "com.smarthealth.ui.MainFrame"]
