# build stage
FROM node:9.11.1-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# production stage
FROM nginx:1.13.12-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html/dist
COPY --from=build-stage /app/index.html /usr/share/nginx/html

# proxy settings
ENV http_proxy "http://127.0.0.1:3001"
ENV https_proxy "http://127.0.0.1:3001"

EXPOSE 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]
