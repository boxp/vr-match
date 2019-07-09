(ns vr-match-back-end.infra.datasource.mysql
  (:require
   [clj-time.jdbc]
   [com.stuartsierra.component :as component]))

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
