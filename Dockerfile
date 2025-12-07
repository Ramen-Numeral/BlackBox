FROM openjdk:27-ea-1-jdk-slim-bookworm

WORKDIR /app

# Copy entire project, excluding what's in .dockerignore
COPY . .

# Accept build argument
ARG ENV_FILE
COPY ${ENV_FILE} .env.secrets

# Optional: output directory for game-generated files
VOLUME ["/app/output"]

# Default command
CMD ["java", "-jar", "build/libs/BlackBox-1.0.jar"]
