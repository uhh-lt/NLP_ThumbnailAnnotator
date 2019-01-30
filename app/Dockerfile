# build stage
FROM node:9.11.1-alpine as build-stage
MAINTAINER Florian Schneider "florian.schneider.1992@gmx.de"
ARG SERVER=http://localhost:8081
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN sed "s/http:\/\/localhost:8081/${SERVER}/g" -i'' ./src/main.js
RUN set "s/..\/assets\/loader.svg/.\/dist\/loader.svg/g" -i'' ./src/components/UserInputCard.vue
RUN npm run build

# production stage
FROM nginx:1.13.12-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html/dist
COPY --from=build-stage /app/index.html /usr/share/nginx/html

EXPOSE 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]
