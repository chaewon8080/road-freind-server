# 1단계: 빌드 Stage
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test

# 2단계: 런타임 Stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
