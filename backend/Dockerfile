# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies trước, chỉ rebuild khi pom.xml đổi
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Chạy bằng user non-root cho an toàn
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Cho phép override JVM options qua env var khi cần (vd: giới hạn RAM trên container nhỏ)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
