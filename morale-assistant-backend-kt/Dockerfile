FROM openjdk:8-jdk-alpine

MAINTAINER kristof.herold@gmail.com

# Create morale-admin user with morale-assistant group
RUN addgroup -S morale-assistant && adduser -S morale-admin -G morale-assistant

USER morale-admin:morale-assistant

ARG JAR_FILE=*.jar

COPY ${JAR_FILE} morale-assistant.jar

CMD ["java", "-jar", "morale-assistant.jar"]
