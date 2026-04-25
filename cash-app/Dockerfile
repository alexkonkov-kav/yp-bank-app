FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/cash-app-0.0.1.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]