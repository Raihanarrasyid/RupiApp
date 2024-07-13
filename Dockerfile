FROM maven:eclipse-temurin AS builder

WORKDIR /app

COPY . /app

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]