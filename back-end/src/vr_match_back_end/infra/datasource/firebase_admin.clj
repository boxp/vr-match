(ns vr-match-back-end.infra.datasource.firebase-admin
  (:import
   (java.io FileInputStream)
   (com.google.firebase FirebaseOptions$Builder FirebaseApp)
   (com.google.auth.oauth2 GoogleCredentials))
  (:require
   [clojure.java.io :as io]
   [com.stuartsierra.component :as component]))

(def service-account-key-file-name "firebase-service-account-key.json")

(defn- init-app
  [database-url]
  (let [credential (GoogleCredentials/fromStream
                    (io/input-stream
                     (io/resource service-account-key-file-name)))]
    (FirebaseApp/initializeApp
     (.. (FirebaseOptions$Builder.)
         (setCredentials credential)
         (setDatabaseUrl database-url)
         build))))

(defrecord FirebaseAdminDatasourceComponent [database-url app]
  component/Lifecycle
  (start [this]
    (println ";; Starting FirebaseAdminDatasourceComponent")
    (-> this
        (assoc :app (init-app database-url))))
  (stop [this]
    (println ";; Stopping FirebaseAdminDatasourceComponent")
    (-> this
        (dissoc :app))))
