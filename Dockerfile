FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -DskipTests package -e

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

