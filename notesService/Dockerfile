FROM openjdk:17-jdk
ARG JAR_FILE=target/*.jar
ARG CFG_FILE=src/main/resources/*.yml
RUN mkdir -p /app/cfg
COPY ${JAR_FILE} /app/notesService-1.0.0.jar
COPY ${CFG_FILE} /app/cfg/notesService-1.0.0.yml
EXPOSE 8090
ENTRYPOINT ["java", "-Dspring.config.location=file:/app/cfg/notesService-1.0.0.yml", "-jar", "/app/notesService-1.0.0.jar"]
