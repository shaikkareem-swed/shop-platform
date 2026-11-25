# One Dockerfile for every module:  docker build --build-arg SERVICE=order-service -t order-service .
FROM maven:3-eclipse-temurin-25 AS build
ARG SERVICE
WORKDIR /workspace
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY product-service/pom.xml product-service/pom.xml
COPY article-service/pom.xml article-service/pom.xml
COPY inventory-service/pom.xml inventory-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml
RUN mvn -B -q -pl ${SERVICE} -am dependency:go-offline || true
COPY . .
RUN mvn -B -q -pl ${SERVICE} -am -DskipTests package

FROM eclipse-temurin:25-jre
ARG SERVICE
RUN groupadd --system app && useradd --system --gid app --create-home app
USER app
WORKDIR /app
COPY --from=build /workspace/${SERVICE}/target/app.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
