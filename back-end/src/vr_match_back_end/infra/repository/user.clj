(ns vr-match-back-end.infra.repository.user
  (:require
   [com.stuartsierra.component :as component]
   [hugsql.core :refer [def-db-fns]]
   [vr-match-back-end.infra.datasource.firebase-admin :as firebase-admin]))

(def-db-fns "vr_match_back_end/infra/repository/sql/user.sql")

(defn register
  [{:keys [firebase-admin-datasource
           mysql-datasource] :as c}
   {:keys [id-token]}]
  (letfn [(insert [uid]
            (insert-user (:db mysql-datasource)
                         {:firebase_id uid
                          :name ""
                          :introduction ""}))]
    (-> firebase-admin-datasource
        :auth
        (.verifyIdToken id-token)
        .getUid
        insert)))

(defrecord UserRepositoryComponent [firebase-admin-datasource]
  component/Lifecycle
  (start [this]
    (println ";; Starting UserRepositoryComponent")
    this)
  (stop [this]
    (println ";; Stopping UserRepositoryComponent")
    this))
