FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/apps/user-api/target/todaii_user_api.jar /jars/user-api.jar
COPY --from=build /app/apps/admin-api/target/todaii_admin_api.jar /jars/admin-api.jar