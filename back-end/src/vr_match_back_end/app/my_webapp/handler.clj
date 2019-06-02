(ns vr-match-back-end.app.my-webapp.handler
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :refer [generate-string parse-string]]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]
            [vr-match-back-end.domain.usecase.example :as example-usecase]
            [vr-match-back-end.app.my-webapp.resolvers :as resolvers]))

(defn index
  [{:keys [example-usecase] :as comp}]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (-> {:message (example-usecase/get-message example-usecase)}

             generate-string)})

(defn graphql
  [{:keys [graphql-schema]} req]
  (let [query (-> req
                  :body
                  slurp
                  (parse-string true)
                  :query)]
    {:status 200
     :headers {}
     :body (-> (execute graphql-schema query nil nil)
               generate-string)}))

(defn- load-schema []
  (-> "resources/graphql-schema.edn"
      slurp
      edn/read-string
      (attach-resolvers {:resolve-approach-list resolvers/approach-list})
      schema/compile))

(defrecord MyWebappHandlerComponent [example-usecase graphql-schema]
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
