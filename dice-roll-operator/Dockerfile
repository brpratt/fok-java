FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/dice-roll-operator-0.0.1-SNAPSHOT.jar dice-roll-operator.jar
ENTRYPOINT ["java", "-jar", "dice-roll-operator.jar"]
