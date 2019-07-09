(ns vr-match-back-end.infra.datasource.firebase-admin
  (:import
   (java.io FileInputStream)
   (com.google.firebase FirebaseOptions$Builder FirebaseApp)
   (com.google.firebase.auth FirebaseAuth)
   (com.google.auth.oauth2 GoogleCredentials))
  (:require
   [clojure.java.io :as io]
   [com.stuartsierra.component :as component]))

(def service-account-key-file-name "firebase-service-account-key.json")

(defn- init-app
  [{:keys [database-url
           credential-str]}]
  (let [credential (GoogleCredentials/fromStream
                    (io/input-stream
                     (.getBytes credential-str)))]
    (FirebaseApp/initializeApp
     (.. (FirebaseOptions$Builder.)
         (setCredentials credential)
         (setDatabaseUrl database-url)
         build)
     (str (gensym)))))

(defrecord FirebaseAdminDatasourceComponent [database-url credential app auth]
  component/Lifecycle
  (start [this]
    (let [application (init-app {:database-url database-url
                                 :credential-str credential})]
      (-> this
          (assoc :app application)
          (assoc :auth (FirebaseAuth/getInstance application)))))
  (stop [this]
    (-> this
        (dissoc :app))))
