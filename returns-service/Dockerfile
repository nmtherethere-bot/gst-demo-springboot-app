FROM openjdk:17-jdk-slim
LABEL authors="Nilkanth There"
WORKDIR /app
COPY target/returnsservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","app.jar"]