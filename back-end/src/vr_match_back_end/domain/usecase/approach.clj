(ns vr-match-back-end.domain.usecase.approach
  (:require
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.infra.repository.user :as ruser]))

(s/def ::approach-usecase
  (s/keys :req-un [::ruser/user-repository]))

(s/def ::session ::euser/session_cookie)
(s/fdef skip
  :args (s/cat :c ::approach-usecase
               :session ::session
               :partner-id ::euser/id))
(defn skip
  [{:keys [user-repository]}
   session
   partner-id]
  (let [me-id (ruser/get-user-id-by-session
               user-repository
               session)]
    (ruser/skip-partner user-repository
                        me-id
                        partner-id)))

(s/fdef favorite
  :args (s/cat :c ::approach-usecase
               :session ::session
               :partner-id ::euser/id)
  :ret (s/keys :req-un [::euser/matched?]))
(defn favorite
  [{:keys [user-repository]}
   session
   partner-id]
  (let [me-id (ruser/get-user-id-by-session
               user-repository
               session)]
    (ruser/favorite-partner user-repository
                            me-id
                            partner-id)
    {:matched? (ruser/get-user-matched? user-repository
                                        me-id
                                        partner-id)}))

(s/fdef reset-all-skip
  :args (s/cat :c ::approach-usecase
               :session ::session)
  :ret nil?)
(defn reset-all-skip
  [{:keys [user-repository]}
   session]
  (let [me-id (ruser/get-user-id-by-session
               user-repository
               session)]
    (ruser/delete-all-skip-from-user me-id)))

(defrecord ApproachUsecase [user-repository]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

