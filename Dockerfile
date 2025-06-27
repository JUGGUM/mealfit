# 1단계: 빌드용 이미지 (JDK 21로 변경)
FROM gradle:8.7.0-jdk21 AS builder

WORKDIR /app
COPY --chown=gradle:gradle . .

RUN ./gradlew clean build -x test --no-daemon --refresh-dependencies

# 2단계: 실행용 이미지
FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
