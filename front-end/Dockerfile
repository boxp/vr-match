FROM node:8.16-alpine as npm
RUN mkdir -p /usr/src/app
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN npm install

FROM clojure:lein-2.8.1-alpine as clojure
RUN mkdir -p /usr/src/app
COPY --from=npm /usr/src/app /usr/src/app
WORKDIR /usr/src/app
RUN lein cljsbuild once prod
RUN lein cljsbuild once server-prod
RUN lein cljsbuild once worker-prod

FROM node:8.16-alpine
RUN mkdir -p /usr/src/app
COPY --from=clojure /usr/src/app /usr/src/app
WORKDIR /usr/src/app
RUN npm run workbox
RUN sed -i -e 's/goog.global.React/global.React/g' -e 's/goog.global.ReactDOM/global.ReactDOM/g' -e 's/goog.global.createReactClass/global.createReactClass/g' target/server/prod/js/compiled/server.js
CMD ["node", "target/server/prod/js/compiled/server.js", "3000"]
