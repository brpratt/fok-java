FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/simplenetes-controller-0.0.1-SNAPSHOT.jar simplenetes-controller.jar
ENTRYPOINT ["java", "-jar", "simplenetes-controller.jar"]
