FROM openjdk:8-jdk-alpine
MAINTAINER Florian Schneider "florian.schneider.1992@gmx.de"

VOLUME /tmp
VOLUME /thumbnailAnnotator 

EXPOSE 8081

# TODO how to fix hard-copy of WordNet when loading from classpath / resource in JAR would be much more elegant and modular
ADD src/main/resources/WordNet-3.0 /tmp/WordNet-3.0/

ARG JAR_FILE
COPY ${JAR_FILE} /thumbnailAnnotator/app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/thumbnailAnnotator/app.jar"]
