# 1단계: 빌드용 이미지
FROM gradle:8.3-jdk17 AS builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle clean build -x test

# 2단계: 실제 실행용 이미지
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
