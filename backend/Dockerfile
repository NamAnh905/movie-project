# --- Build stage ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# --- Run stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV PORT=8080
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Render đặt biến PORT động → truyền vào server.port
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
