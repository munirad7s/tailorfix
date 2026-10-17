FROM gradle:8.14-jdk21 AS build
WORKDIR /workspace
COPY backend/ ./backend/
COPY api/ ./api/
WORKDIR /workspace/backend
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
