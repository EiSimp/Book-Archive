FROM jelastic/maven:3.9.4-openjdk-22.ea-b17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:22-slim
COPY --from=build /target/bookarchive-0.0.1-SNAPSHOT.jar bookarchive.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "bookarchive.jar"]
