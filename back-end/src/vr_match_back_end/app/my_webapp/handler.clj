(ns vr-match-back-end.app.my-webapp.handler
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :refer [generate-string parse-string]]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]
            [vr-match-back-end.app.my-webapp.resolvers :as resolvers]))

(defn index
  [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (-> {:message "hello!"}
             generate-string)})

(defn graphql
  [{:keys [graphql-schema my-webapp-resolvers]} req]
  (let [query (-> req
                  :body
                  slurp
                  (parse-string true)
                  :query)]
    {:status 200
     :headers {}
     :body (-> (execute graphql-schema
                        query
                        nil
                        my-webapp-resolvers)
               generate-string)}))

(defn- load-schema []
  (-> "resources/graphql-schema.edn"
      slurp
      edn/read-string
      (attach-resolvers {:resolve-approach-list resolvers/approach-list
                         :resolve-register-user resolvers/register-user
                         :resolve-login-user resolvers/login-user})
      schema/compile))

(defrecord MyWebappHandlerComponent [graphql-schema my-webapp-resolvers]
  component/Lifecycle
  (start [this]
    (println ";; Starting MyWebappHandlerComponent")
    (-> this
        (assoc :graphql-schema (load-schema))))
  (stop [this]
    (println ";; Stopping MyWebappHandlerComponent")
    (-> this
        (dissoc :graphql-schema))))

(defn my-webapp-handler-component
  []
  (map->MyWebappHandlerComponent {}))
