(ns vr-match-back-end.infra.repository.auth
  (:require [com.stuartsierra.component :as component]
            [vr-match-back-end.infra.datasource.firebase-admin :as firebase-admin]))

(defn register
  [{:keys [firebase-admin-datasource] :as c}
   {:keys [id-token]}]
  (-> firebase-admin-datasource
      :auth
      (.verifyIdToken id-token)
      .getUid
      ;; TODO: DBへの登録処理実装
      ))

(defrecord AuthRepositoryComponent [firebase-admin-datasource]
  component/Lifecycle
  (start [this]
    (println ";; Starting AuthRepositoryComponent")
    this)
  (stop [this]
    (println ";; Stopping AuthRepositoryComponent")
    this))
