# FROM eclipse-temurin:21-jre-jammy
#Andrew
FROM amazoncorretto:21
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]