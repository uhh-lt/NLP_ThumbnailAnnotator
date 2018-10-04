# Admin Guide
Please note that an <b>internet connection is crucial mandatory!</b>
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
- ##### Clone repository
  ```bash
  git clone https://github.com/floschne/NLP_ThumbnailAnnotator.git
  ```
  
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
