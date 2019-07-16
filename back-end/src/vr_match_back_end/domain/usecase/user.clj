(ns vr-match-back-end.domain.usecase.user
  (:require
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.domain.entity.image :as eimage]
   [vr-match-back-end.infra.repository.user :as ruser]))

(s/def ::user-usecase
  (s/keys :req-un [::ruser/user-repository]))

(s/def ::session ::euser/session_cookie)
(s/def :update-user-params/image-ids (s/coll-of ::eimage/id))
(s/def :update-user/params
  (s/keys :opt-un [::euser/name
                   ::euser/introduction
                   :update-user-params/image-ids
                   ::euser/platforms]))
(s/fdef update-user
  :args (s/cat :c ::user-usecase
               :session ::session
               :params :update-user/params)
  :ret nil?)
(defn update-me
  [{:keys [user-repository]}
   session
   params]
  (let [user-id (ruser/get-user-id-by-session
                 user-repository
                 session)]
    (ruser/update-user
     user-repository
     (-> params
         (assoc :id user-id)))))

(s/fdef get-me
  :args (s/cat :c ::user-usecase
               :session ::session
               :with-images? boolean?
               :with-platforms? boolean?)
  :ret ::euser/user)
(defn get-me
  [{:keys [user-repository]}
   session
   with-images?
   with-platforms?]
  (let [user-id (ruser/get-user-id-by-session
                 user-repository
                 session)]
    (ruser/get-user-by-id
     user-repository
     user-id
     with-images?
     with-platforms?)))

(defrecord UserUsecase [user-repository]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))
