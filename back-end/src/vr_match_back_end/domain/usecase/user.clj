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

(s/def :paging-params/offset number?)
(s/def :paging-params/limit number?)
(s/def ::paging-params
  (s/keys :opt-un [:paging-params/offset :paging-params/limit]))
(s/def :get-my-recommended-users-result/total number?)
(s/def :get-my-recommended-users-result/users (s/coll-of ::euser/user))
(s/def ::get-my-recommended-users-result
  (s/keys :req-un [:get-my-recommended-users-result/users]
          :opt-un [:get-my-recommended-users-result/total]))
(s/fdef get-my-recommended-users
  :args (s/cat :c ::user-usecase
               :session ::session
               :with-images? boolean?
               :with-platforms? boolean?
               :with-total? boolean?
               :paging-params ::paging-params)
  :ret ::get-my-recommended-users-result)
(defn get-my-recommended-users
  [{:keys [user-repository]}
   session
   with-images?
   with-platforms?
   with-total?
   paging-params]
  (let [user-id (ruser/get-user-id-by-session
                 user-repository
                 session)]
    (ruser/get-recommended-users-by-user-id
     user-repository
     user-id
     with-images?
     with-platforms?
     with-total?
     paging-params)))

(s/fdef get-favorited-users-from-me
  :args (s/cat :c ::user-usecase
               :session ::session
               :with-images? boolean?
               :with-platforms? boolean?
               :with-total? boolean?
               :paging-params ::ruser/get-favorited-users-from-user-id-paging-params)
  :ret ::ruser/get-favorited-users-from-user-id-result)
(defn get-favorited-users-from-me
  [{:keys [user-repository]}
   session
   with-images?
   with-platforms?
   with-total?
   paging-params]
  (let [user-id (ruser/get-user-id-by-session
                 user-repository
                 session)]
    (ruser/get-favorited-users-from-user-id
     user-repository
     user-id
     with-images?
     with-platforms?
     with-total?
     paging-params)))

(s/fdef get-my-matched-users
  :args (s/cat :c ::user-usecase
               :session ::session
               :with-images? boolean?
               :with-platforms? boolean?
               :with-has-next? boolean?
               :paging-params ::ruser/get-matched-users-by-user-id-paging-params)
  :ret ::ruser/get-matched-users-by-user-id-result)
(defn get-my-matched-users
  [{:keys [user-repository]}
   session
   with-images?
   with-platforms?
   with-has-next?
   paging-params]
  (let [user-id (ruser/get-user-id-by-session
                 user-repository
                 session)]
    (ruser/get-matched-users-by-user-id
     user-repository
     user-id
     with-images?
     with-platforms?
     with-has-next?
     paging-params)))

(defrecord UserUsecase [user-repository]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))
