(ns vr-match-back-end.infra.datasource.mysql
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.jdbc]
   [com.stuartsierra.component :as component]))

(s/def ::dbname string?)
(s/def ::user string?)
(s/def ::password string?)
(s/def :db/dbtype string?)
(s/def ::db
  (s/keys :req-un [::dbname ::user ::password :db/dbtype]))
(s/def ::mysql-datasource
  (s/keys :req-un [::dbname ::user ::password]
          :opt-un [::db]))

(defn init-db
  [{:keys [dbname user password] :as params}]
  (assoc params :dbtype "mysql"))

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
