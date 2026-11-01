# Build stage: Maven + JDK 21 (matches <java.version>21</java.version> in pom.xml)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

# Runtime stage: slim JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/Hotels-rest-*.jar /app/app.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
