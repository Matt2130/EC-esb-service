# Etapa 1: Compilación con Maven y JDK 17
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera con JRE 17
FROM eclipse-temurin:17-jre-alpine

ENV JAVA_OPTS="-Xmx512m -Xms128m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]