FROM eclipse-temurin:21-jre
WORKDIR /app
COPY weblog-web/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]