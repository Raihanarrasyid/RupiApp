FROM maven:eclipse-temurin AS builder

WORKDIR /app

COPY . /app

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/app.jar

COPY --from=builder /app/images /app/images

COPY --from=builder /app/uploads /app/uploads

ENTRYPOINT ["java", "-jar", "/app/app.jar"]