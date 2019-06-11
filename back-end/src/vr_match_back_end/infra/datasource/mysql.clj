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
    (println ";; Starting MysqlDatasourceComponent")
    (-> this
        (assoc :db (init-db {:dbname dbname
                             :user user
                             :password password}))))
  (stop [this]
    (println ";; Stopping MysqlDatasourceComponent")
    (-> this
        (dissoc :db))))
