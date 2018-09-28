FROM openjdk:8-jdk-alpine
MAINTAINER Florian Schneider "florian.schneider.1992@gmx.de"

ARG JAR_FILE

VOLUME /tmp

VOLUME /thumbnailAnnotator 
COPY target/lib /thumbnailAnnotator/lib 
COPY target/thumbnailAnnotator.api-0.0.1-SNAPSHOT.jar /thumbnailAnnotator/app.jar

EXPOSE 8081

# proxy settings
ENV http_proxy "http://127.0.0.1:3001"
ENV https_proxy "http://127.0.0.1:3001"

ENTRYPOINT ["java","-jar","/thumbnailAnnotator/app.jar"]
