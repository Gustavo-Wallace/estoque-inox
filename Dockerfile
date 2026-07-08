FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/estoque-inox-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=postgres
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
