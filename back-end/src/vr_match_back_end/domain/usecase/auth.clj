(ns vr-match-back-end.domain.usecase.auth
  (:require
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.infra.repository.user :as ruser]))

(s/def ::user-repository record?)
(s/fdef register
  :args (s/cat :c (s/keys :req-un [::user-repository])
               :idToken string?)
  :ret ::euser/user)
(defn register
  [{:keys [user-repository] :as c}
   idToken]
  (ruser/create-new-user user-repository {:id-token idToken}))

(defrecord AuthUsecaseComponent [user-repository]
  component/Lifecycle
  (start [this]
    (println ";; Starting AuthUsecaseComponent")
    this)
  (stop [this]
    (println ";; Stopping AuthUsecaseComponent")
    this))
