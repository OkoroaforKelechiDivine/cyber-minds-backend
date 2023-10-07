FROM openjdk:17-alpine
WORKDIR /backend
COPY ./target/*.jar /backend/backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "backend.jar"]
