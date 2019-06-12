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
                                         (setExpiresIn (-> (t/days 5)
                                                           to-long))
                                         build))))

(s/fdef register
  :args (s/cat :c (s/keys :req [::firebase-admin-datasource
                                ::mysql-datasource])
               :params (s/keys :req [::id-token]))
  :ret ::euser/user)
(defn register
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
    (-> (user-by-id id)
        (assoc :session_cookie session_cookie))))

(defrecord UserRepositoryComponent [firebase-admin-datasource]
  component/Lifecycle
  (start [this]
    (println ";; Starting UserRepositoryComponent")
    this)
  (stop [this]
    (println ";; Stopping UserRepositoryComponent")
    this))
