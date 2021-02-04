FROM adoptopenjdk/openjdk8:alpine

MAINTAINER kristof.herold@gmail.com

# Create morale-admin user with morale-assistant group
RUN addgroup -S morale-assistant && adduser -S morale-admin -G morale-assistant

RUN mkdir -p /morale-assistant
RUN chown morale-admin /morale-assistant

USER morale-admin:morale-assistant

WORKDIR /morale-assistant

ARG JAR_FILE=*.jar

# COPY google_credentials.json config/google_credentials.json
COPY ${JAR_FILE} morale-assistant.jar


CMD [ "java", "-jar", "-Dspring.profiles.active=deploy", "morale-assistant.jar" ]
