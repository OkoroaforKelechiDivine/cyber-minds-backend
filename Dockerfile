FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /backend
COPY . /backend/
RUN mvn clean package

#
# Package stage
#
FROM openjdk:17-alpine
WORKDIR /backend
COPY --from=build /backend/target/*.jar /backend/backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","backend.jar"]