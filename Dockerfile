#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package

#
# Runtime stage
#
FROM adoptopenjdk:17-jre-hotspot
COPY --from=build /target/backend-0.0.1-SNAPSHOT.jar backend.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "backend.jar"]
