FROM openjdk:17-jdk-slim
LABEL authors="Nilkanth There"
WORKDIR /app
COPY target/userservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]