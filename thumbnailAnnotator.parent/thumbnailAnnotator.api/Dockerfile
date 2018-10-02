FROM openjdk:8-jdk-alpine
MAINTAINER Florian Schneider "florian.schneider.1992@gmx.de"

VOLUME /tmp
VOLUME /thumbnailAnnotator 

EXPOSE 8081

#ARG JAR_FILE
#COPY ${JAR_FILE} /thumbnailAnnotator/app.jar
# TODO how to fix hard-copy of WordNet when loading from classpath / resource in JAR would be much more elegant and modular
ADD src/main/resources/WordNet-3.0 /tmp/WordNet-3.0/
# TODO how to fix hard-coded jar name?! Problem is that JAR_FILE gets not resolved / setted correctly from maven plugin...
COPY target/thumbnailAnnotator.api-0.0.1-SNAPSHOT.jar /thumbnailAnnotator/app.jar

ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/thumbnailAnnotator/app.jar"]
