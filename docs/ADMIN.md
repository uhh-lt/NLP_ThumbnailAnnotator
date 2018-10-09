# Admin Guide
Please note that an <b>internet connection is crucial mandatory!</b> Also be aware that this application (specially for testing) <b>needs a lot of heap space (~3-4 GB) </b> due to the models used in the UIMA Pipeline. 
## Install prerequisites
- Java 8
- Maven 3
- redis-server >= 3.2
- npm
- nginx
- docker
- docker-compose >= 3.0

```bash
sudo apt update && sudo apt upgrade -y 
sudo apt install -y oracle-java8-installer oracle-java8-set-default maven npm nginx redis-server docker docker-compose
```

## Clone repository
  ```bash
  git clone https://github.com/floschne/NLP_ThumbnailAnnotator.git && cd NLP_ThumbnailAnnotator
  ```

## Apply JWNL Properties File 'Bugfix'
When using WordNet with DKPro WSD, a hard coded, non-relative path to the WordNet files has to be set in a properties file. Since the absolute location of those WordNet files is not known at runtime, the temporary solution is to copy the files to /tmp/.
  ```bash
  cp -r thumbnailAnnotator.parent/thumbnailAnnotator.core/src/main/resources/WordNet-3.0 /tmp/
  ```

## Run with docker-compose

- ##### Package and the java-based REST API
  ```bash
  cd thumbnailAnnotator.parent
  mvn clean package spring-boot:repackage
  cd ..
  ```
- ##### Run
  ```bash
  docker-compose up --build
  ```
- ##### Open Browser
  - [localhost:80](http://localhost:80) for Web-Frontend
  - [localhost:8081](http://localhost:8081) for Swagger-UI 

## Run on a local (Debian based) machine (without Docker)

- ##### Start the redis-server  
  ```bash
  sudo service redis-server start
  ```
  
- ##### Package and run the java-based REST API
  ```bash
  cd thumbnailAnnotator.parent
  mvn clean package spring-boot:repackage
  java -jar -Dspring.profiles.active=local thumbnailAnnotator.api/target/thumbnailAnnotator.api-0.1.BETA.jar &
  cd ..
  ```
- ##### Build and run the Vue.js-based Web-Frontend
  ```bash
  cd app
  npm run build
  sudo cp -r dist index.html /var/www/html/
  sudo service nginx restart
  cd ..
  ```
- ##### Open Browser
  - [localhost:80](http://localhost:80) for Web-Frontend
  - [localhost:8081](http://localhost:8081) for Swagger-UI 
