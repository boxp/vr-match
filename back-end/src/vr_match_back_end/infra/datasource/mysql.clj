(ns vr-match-back-end.infra.datasource.mysql
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.jdbc]
   [com.stuartsierra.component :as component]
   [integrant.core :as ig]))

(s/def ::user string?)
(s/def ::password string?)
(s/def :db/classname string?)
(s/def :db/subprotocol string?)
(s/def :db/subname string?)
(s/def ::db
  (s/keys :req-un [::user ::password :db/classname :db/subprotocol :db/subprotocol]))
(s/def ::mysql-datasource
  (s/keys :req-un [::db]
          :opt-un [::dbname ::user ::password]))

(defn init-db
  [{:keys [dbname user password] :as params}]
  (-> {}
      (assoc :classname "com.mysql.jdbc.Driver")
      (assoc :subprotocol "mysql")
      (assoc :subname (str "//127.0.0.1:3306/" dbname "?connectionCollation=utf8mb4_bin"))
      (assoc :user user)
      (assoc :password password)))

(defrecord MysqlDatasourceComponent [dbname user password db]
  component/Lifecycle
  (start [this]
    (-> this
        (assoc :db (init-db {:dbname dbname
                             :user user
                             :password password}))))
  (stop [this]
    (-> this
        (dissoc :db))))

(s/def ::dbname string?)

(defmethod ig/init-key ::mysql-datasource [_ {:keys [dbname user password]}]
  (init-db {:dbname dbname
            :user user
            :password password}))

(defmethod ig/halt-key! ::mysql-datasource [_ _]
  nil)

(defmethod ig/prep-key ::mysql-datasource [_ config]
  (merge {:dbname "vr_match"
          :user "root"
          :password ""}
         config))

(defmethod ig/pre-init-spec ::mysql-datasource [_]
  (s/keys :req-un [::dbname ::user ::password]))
