(ns vr-match-back-end.domain.usecase.auth
  (:require
   [integrant.core :as ig]
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.infra.repository.user :as ruser]))

(s/def ::user-repository record?)
(s/fdef register
  :args (s/cat :c (s/keys :req-un [::user-repository])
               :id-token string?)
  :ret ::euser/user)
(defn register
  [{:keys [user-repository] :as c}
   id-token]
  (ruser/create-new-user user-repository {:id-token id-token}))

(defn login
  [{:keys [user-repository] :as c}
   id-token]
  (ruser/renew-user-session user-repository {:id-token id-token}))

(defmethod ig/init-key ::auth-usecase [_ u] u)

(defmethod ig/halt-key! ::auth-usecase [_ _] nil)

(defmethod ig/assert-key ::auth-usecase [_ {:keys [user-repository]}]
  (assert user-repository "user-repository is required"))
