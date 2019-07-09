(ns vr-match-back-end.infra.repository.user
  (:import
   (java.util.concurrent TimeUnit)
   (com.google.firebase.auth SessionCookieOptions))
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.core :as t]
   [clj-time.coerce :refer [to-long]]
   [com.stuartsierra.component :as component]
   [hugsql.core :refer [def-db-fns]]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.infra.datasource.firebase-admin :as firebase-admin]))

(def-db-fns "vr_match_back_end/infra/repository/sql/user.sql")

(s/def ::firebase-admin-datasource record?)
(s/def ::id-token string?)
(s/def ::mysql-datasource record?)

(s/fdef get-firebase_id
  :args (s/cat :firebase-admin-datasource ::firebase-admin-datasource
               :id-token ::id-token)
  :ret ::euser/firebase_id)
(defn- get-firebase_id
  [firebase-admin-datasource id-token]
  (-> firebase-admin-datasource
      :auth
      (.verifyIdToken id-token)
      .getUid))

(s/fdef get-cookie_session
  :args (s/cat :firebase-admin-datasource ::firebase-admin-datasource
               :id-token ::id-token)
  :ret ::euser/session_cookie)
(defn- get-session_cookie
  [firebase-admin-datasource
   id-token]
  (-> firebase-admin-datasource
      :auth
      (.createSessionCookie id-token (.. (SessionCookieOptions/builder)
                                         (setExpiresIn (* 5 24 60 60 1000))
                                         build))))

(s/fdef create-new-user
  :args (s/cat :c (s/keys :req-un [::firebase-admin-datasource
                                   ::mysql-datasource])
               :params (s/keys :req-un [::id-token]))
  :ret ::euser/user)
(defn create-new-user
  [{:keys [firebase-admin-datasource
           mysql-datasource] :as c}
   {:keys [id-token] :as params}]
  (let [firebase_id (get-firebase_id
                     firebase-admin-datasource
                     id-token)
        session_cookie (get-session_cookie firebase-admin-datasource
                                           id-token)
        id (insert-user (:db mysql-datasource)
                        {:firebase_id firebase_id
                         :name ""
                         :introduction ""})]
    (-> (user-by-firebase_id (:db mysql-datasource)
                             {:firebase_id firebase_id})
        (assoc :session_cookie session_cookie))))

(s/fdef renew-user-session
  :args (s/cat :c (s/keys :req-un [::firebase-admin-datasource
                                   ::mysql-datasource])
               :params (s/keys :req-un [::id-token]))
  :ret ::euser/user)
(defn renew-user-session
  [{:keys [firebase-admin-datasource
           mysql-datasource] :as c}
   {:keys [id-token] :as params}]
  (let [firebase_id (get-firebase_id
                     firebase-admin-datasource
                     id-token)
        session_cookie (get-session_cookie
                        firebase-admin-datasource
                        id-token)]
    (-> (user-by-firebase_id (:db mysql-datasource)
                             {:firebase_id firebase_id})
        (assoc :session_cookie session_cookie))))

(s/fdef update-user
  :args (s/cat :c (s/keys :req-un [::mysql-datasource])
               :params (s/keys :req-un [::euser/id]
                               :opt-un [::euser/name
                                        ::euser/introduction]))
  :ret ::euser/user)
(defn update-user
  [{:keys [mysql-datasource]}
   params]
  (update-user-by-id (:db mysql-datasource) params)
  (user-by-id (:db mysql-datasource) {:id (:id params)}))

(defrecord UserRepositoryComponent [firebase-admin-datasource]
  component/Lifecycle
  (start [this]
    this)
  (stop [this]
    this))
