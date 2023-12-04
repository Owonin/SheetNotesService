FROM openjdk:17-jdk
ARG JAR_FILE=target/*.jar
ARG CFG_FILE=src/main/resources/*.yml
RUN mkdir -p /app/cfg
COPY ${JAR_FILE} /app/authService-1.0.0.jar
COPY ${CFG_FILE} /app/cfg/authService-1.0.0.yml
EXPOSE 8090
ENTRYPOINT ["java", "-Dspring.config.location=file:/app/cfg/authService-1.0.0", "-jar", "/app/authService-1.0.0.jar"]
