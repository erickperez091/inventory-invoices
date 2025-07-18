# Etapa 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
# Copiamos el código fuente
COPY . .

# Copiamos settings.xml con credenciales para Nexus (debe estar en el root del proyecto)
COPY settings.xml /root/.m2/settings.xml
RUN cat /root/.m2/settings.xml
# Compilamos el microservicio y descargamos la librería desde Nexus
ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true"
RUN mvn clean package -s /root/.m2/settings.xml

# Etapa 2: Imagen final con Java
FROM eclipse-temurin:21-alpine
WORKDIR /app

# Copiamos el JAR generado desde el builder
COPY --from=builder /app/target/*.jar app.jar
ENV SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/invoice?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
ENV SERVER_DISCOVERY="http://host.docker.internal:8761/eureka"
ENV KAFKA_HOST="host.docker.internal:9092"

EXPOSE 9081

ENTRYPOINT ["java", "-jar", "app.jar"]
