(ns vr-match-back-end.infra.datasource.firebase-admin
  (:import
   (java.io FileInputStream)
   (com.google.firebase FirebaseOptions$Builder FirebaseApp)
   (com.google.firebase.auth FirebaseAuth)
   (com.google.auth.oauth2 GoogleCredentials))
  (:require
   [clojure.java.io :as io]
   [com.stuartsierra.component :as component]
   [integrant.core :as ig]
   [clojure.spec.alpha :as s]))

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

(s/def ::database-url string?)
(s/def ::credential string?)

(defmethod ig/init-key ::firebase-admin-datasource [_ {:keys [database-url credential] :as d}]
  (let [application (init-app {:database-url database-url
                               :credential-str credential})]
    (-> d
        (assoc :app application)
        (assoc :auth (FirebaseAuth/getInstance application)))))

(defmethod ig/halt-key! ::firebase-admin-datasource [_ m]
  (-> m
      (dissoc :app)
      (dissoc :auth)))

(defmethod ig/prep-key ::firebase-admin-datasource [_ config]
  (merge config {:database-url "https://vr-match.firebaseio.com"
                 :credential ""}))

(defmethod ig/assert-key ::firebase-admin-datasource [_ {:keys [database-url credential]}]
  (assert database-url "database-url is required")
  (assert credential "credential is required"))
