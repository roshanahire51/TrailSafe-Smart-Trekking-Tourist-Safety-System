# Multi-stage Dockerfile for TrailSafe (Java 21)
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy project files
COPY src ./src
COPY public ./public
COPY database ./database

# Compile backend
RUN mkdir -p bin && javac -encoding UTF-8 -d bin $(find src -name "*.java")

# Minimal runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/bin ./bin
COPY --from=builder /app/public ./public
COPY --from=builder /app/database ./database

ENV PORT=8080
EXPOSE 8080

CMD ["sh", "-c", "java -cp bin com.trailsafe.server.TrailSafeServer $PORT"]
