(ns vr-match-back-end.app.my-webapp.handler
  (:require
   [clojure.edn :as edn]
   [clojure.stacktrace :refer [print-stack-trace]]
   [com.stuartsierra.component :as component]
   [cheshire.core :refer [generate-string parse-string]]
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
  (try
    (let [session (or (some-> req :headers (get "session")) "")
          query (-> req :body slurp (parse-string true) :query)
          result (execute graphql-schema
                          query
                          nil
                          (if-not (nil? session)
                            (merge my-webapp-resolvers {:session session})
                            my-webapp-resolvers))]
      {:status (if (-> result :errors seq)
                 400
                 200)
       :headers {}
       :body (-> result
                 generate-string)})
    (catch Exception e
      (print-stack-trace e)
      {:status 400
       :headers {}
       :body "Something has wrong."})))

(defn- load-schema []
  (-> "resources/graphql-schema.edn"
      slurp
      edn/read-string
      (attach-resolvers {:resolve-approach-list resolvers/approach-list
                         :resolve-register-user resolvers/register-user
                         :resolve-login-user resolvers/login-user
                         :resolve-upload-image resolvers/upload-image
                         :resolve-update-me resolvers/update-me
                         :resolve-me resolvers/me
                         :resolve-skip resolvers/skip
                         :resolve-favorite resolvers/favorite
                         :resolve-platform-options resolvers/platform-options
                         :resolve-favorited-users resolvers/favorited-users
                         :resolve-matched-users resolvers/matched-users})
      schema/compile))

(defrecord MyWebappHandlerComponent [graphql-schema my-webapp-resolvers]
  component/Lifecycle
  (start [this]
    (-> this
        (assoc :graphql-schema (load-schema))))
  (stop [this]
    (-> this
        (dissoc :graphql-schema))))

(defn my-webapp-handler-component
  []
  (map->MyWebappHandlerComponent {}))
