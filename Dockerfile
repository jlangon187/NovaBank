FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY cliente-service/pom.xml cliente-service/pom.xml
COPY cuenta-service/pom.xml cuenta-service/pom.xml
COPY operacion-service/pom.xml operacion-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY exchange-rate-mock-service/pom.xml exchange-rate-mock-service/pom.xml
RUN mvn --batch-mode dependency:go-offline

COPY eureka-server eureka-server
COPY config-server config-server
COPY cliente-service cliente-service
COPY cuenta-service cuenta-service
COPY operacion-service operacion-service
COPY api-gateway api-gateway
COPY auth-service auth-service
COPY exchange-rate-mock-service exchange-rate-mock-service
RUN mvn --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:17-jre-jammy

ARG MODULE
WORKDIR /app
COPY --from=build /workspace/${MODULE}/target/${MODULE}-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
