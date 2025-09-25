FROM openjdk:17-jdk-slim
LABEL authors="Nilkanth There"
WORKDIR /app
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
