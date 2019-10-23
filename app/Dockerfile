# build stage
FROM node:lts-alpine as build-stage
MAINTAINER Florian Schneider "florian.schneider.1992@gmx.de"
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN sed "s/localhost:/api:/g" -i'' ./src/index.js
RUN npm run build
# production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
