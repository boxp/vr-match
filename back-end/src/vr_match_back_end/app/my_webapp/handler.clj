(ns vr-match-back-end.app.my-webapp.handler
  (:require
   [integrant.core :as ig]
   [clojure.spec.alpha :as s]
   [clojure.edn :as edn]
   [clojure.tools.logging :as log]
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
    (let [session (or (some-> req :headers (get "session"))
                      (some-> req :query-params (get "session"))
                      "")
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
      (log/error e)
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
                         :resolve-matched-users resolvers/matched-users
                         :resolve-favorited-from-users resolvers/favorited-from-users
                         :resolve-partner resolvers/partner
                         :resolve-reset-all-skip resolvers/reset-all-skip})
      schema/compile))

(defmethod ig/init-key ::my-webapp-handler [_ h]
  (-> h
      (assoc :graphql-schema (load-schema))))

(defmethod ig/halt-key! ::my-webapp-handler [_ _] nil)

(defmethod ig/pre-init-spec ::my-webapp-handler [_]
  (s/keys :req-un [:resolvers/my-webapp-resolvers]))
