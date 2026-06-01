# ===== BUILD STAGE =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# copy toàn bộ project (để resolve multi-module dependency)
COPY ../../ .

# build only user-api
RUN mvn clean package -pl apps/user-api -am -DskipTests

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# copy jar
COPY --from=build /app/apps/user-api/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]