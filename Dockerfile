FROM maven:3.8.4 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean compile package

FROM amazoncorretto:21

WORKDIR /app

COPY --from=build /app/target/shop-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "shop-0.0.1-SNAPSHOT.jar"]
