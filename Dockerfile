FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY backend/backend .

RUN chmod +x mvnw && ./mvnw clean package

CMD ["java","-jar","target/backend-0.0.1-SNAPSHOT.jar"]
