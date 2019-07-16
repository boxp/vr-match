(ns vr-match-back-end.infra.datasource.mysql
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.jdbc]
   [com.stuartsierra.component :as component]))

(s/def ::user string?)
(s/def ::password string?)
(s/def :db/classname string?)
(s/def :db/subprotocol string?)
(s/def :db/subname string?)
(s/def ::db
  (s/keys :req-un [::user ::password :db/classname :db/subprotocol :db/subprotocol]))
(s/def ::mysql-datasource
  (s/keys :req-un [::dbname ::user ::password]
          :opt-un [::db]))

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
