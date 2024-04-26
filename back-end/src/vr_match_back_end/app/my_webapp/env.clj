(ns vr-match-back-end.app.my-webapp.env
  (:require
   [integrant.core :as ig]
   [environ.core :refer [env]]))

(defmethod ig/init-key ::client-origin [_ _] (or (env :vr-match-client-origin) "http://localhost:8888"))
(defmethod ig/init-key ::port [_ _] (-> (or (env :vr-match-back-end-my-webapp-port) (env :port) "8080") Integer/parseInt))
(defmethod ig/init-key ::firebase-database-url [_ _] (or (env :vr-match-back-end-firebase-database-url) "https://vr-match.firebaseio.com"))
(defmethod ig/init-key ::firebase-service-account-key [_ _] (or (env :vr-match-firebase-service-account-key) ""))
(defmethod ig/init-key ::mysql-dbname [_ _] (or (env :vr-match-mysql-dbname) "vr_match"))
(defmethod ig/init-key ::mysql-user [_ _] (or (env :vr-match-mysql-user) "root"))
(defmethod ig/init-key ::mysql-password [_ _] (or (env :vr-match-mysql-password) ""))
(defmethod ig/init-key ::mysql-hostname [_ _] (or (env :vr-match-mysql-hostname) "127.0.0.1"))
(defmethod ig/init-key ::mysql-port [_ _] (or (env :vr-match-mysql-port) "3306"))
(defmethod ig/init-key ::cloud-storage-bucket-name [_ _] (or (env :vr-match-cloud-storage-bucket-name) ""))
