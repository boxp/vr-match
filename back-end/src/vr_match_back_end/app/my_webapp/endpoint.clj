(ns vr-match-back-end.app.my-webapp.endpoint
  (:require
   [integrant.core :as ig]
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [compojure.core :refer [defroutes context GET POST OPTIONS routes]]
   [compojure.route :as route]
   [ring.adapter.jetty :as server]
   [vr-match-back-end.app.my-webapp.handler :as handler]))

(defn wrap-header-csp
  [handler origin]
  (fn [request]
    (assoc-in (handler request)
              [:headers "Content-Security-Policy"]
              (str "default-src " origin))))

(defn wrap-header-cors
  [handler origin]
  (fn [request]
    (-> (handler request)
        (assoc-in [:headers "Access-Control-Allow-Origin"] origin)
        (assoc-in [:headers "Access-Control-Allow-Credentials"] "true"))))

(defn main-routes
  [{:keys [my-webapp-handler] :as comp}]
  (routes
   (GET "/" [] (handler/index my-webapp-handler))
   (POST "/graphql" req (handler/graphql my-webapp-handler req))
   (OPTIONS "*" []
     {:status 200
      :headers {"Access-Control-Allow-Methods" "POST, GET, OPTIONS"
                "Access-Control-Allow-Credentials" "true"
                "Access-Control-Allow-Headers" "Content-Type, Session"}})
   (route/not-found {:status 404
                     :headers {}
                     :body "<h1>404 page not found</h1>"})))

(defn app
  [{:keys [client-origin] :as comp}]
  (-> (main-routes comp)
      (wrap-header-csp client-origin)
      (wrap-header-cors client-origin)))

(s/def ::port number?)
(s/def ::client-origin string?)

(defmethod ig/init-key ::endpoint [_ {:keys [port] :as h}]
  (-> h
      (assoc :server (server/run-jetty (app h) {:port port :join? false}))))

(defmethod ig/halt-key! ::endpoint [_ h]
  (do
    (-> h :server .stop)
    nil))

(defmethod ig/pre-init-spec ::endpoint [_]
  (s/keys
   :req-un [::port
            ::client-origin
            :handler/my-webapp-handler]))
