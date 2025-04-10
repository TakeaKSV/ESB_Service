FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
# Copiar el pom.xml y el directorio src
COPY pom.xml .
COPY src ./src
# Descargar las dependencias y compilar el proyecto
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
#Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR de la etapa builder
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xmx256m"

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]