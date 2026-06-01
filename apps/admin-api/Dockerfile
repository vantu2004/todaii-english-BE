# ===== BUILD STAGE =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# copy toàn bộ project
COPY ../../ .

# build only admin-api
RUN mvn clean package -pl apps/admin-api -am -DskipTests

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/apps/admin-api/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]