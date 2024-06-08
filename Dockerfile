FROM openjdk:17.0.2-jdk
WORKDIR /app/mp3-app
COPY build/libs/processService-1.0-SNAPSHOT.jar /app/mp3-app/processService.jar
ENTRYPOINT ["java", "-jar", "processService.jar"]