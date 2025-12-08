FROM openjdk:27-ea-1-jdk-slim-bookworm

WORKDIR /app

COPY . .

VOLUME ["/app/output"]

CMD ["java", "-jar", "build/libs/BlackBox-1.0.jar"]
