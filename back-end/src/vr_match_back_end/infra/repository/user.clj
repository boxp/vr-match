(ns vr-match-back-end.infra.repository.user
  (:import
   (java.util.concurrent TimeUnit)
   (com.google.firebase.auth SessionCookieOptions))
  (:require
   [clojure.spec.alpha :as s]
   [clojure.set :as set]
   [clojure.java.jdbc :as jdbc]
   [clj-time.core :as t]
   [clj-time.coerce :refer [to-long]]
   [com.stuartsierra.component :as component]
   [hugsql.core :refer [def-db-fns]]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.domain.entity.image :as eimage]
   [vr-match-back-end.infra.datasource.firebase-admin :as firebase-admin]))

(def-db-fns "vr_match_back_end/infra/repository/sql/user.sql")
(def-db-fns "vr_match_back_end/infra/repository/sql/user_image.sql")

(s/def ::firebase-admin-datasource record?)
(s/def ::mysql-datasource record?)
(s/def ::id-token string?)

(s/def ::user-repository
  (s/keys :req-un [::firebase-admin-datasource
                   ::mysql-datasource]))

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
  :args (s/cat :c ::user-repository
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
        [_ id] (insert-user (:db mysql-datasource)
                            {:firebase_id firebase_id
                             :name ""
                             :introduction ""})]
    (-> (user-by-id (:db mysql-datasource)
                    {:id id})
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
                        id-token)
        user (user-by-firebase_id (:db mysql-datasource)
                                  {:firebase_id firebase_id})]
    (if (seq user)
      (assoc user :session_cookie session_cookie)
      (throw (ex-info "未登録のユーザーです"
                      {:type :unregisterd-user
                       :firebase_id firebase_id})))))


(s/def :image-record/id number?)
(s/def :image-record/url string?)
(s/def :image-record/image_type number?)
(s/def ::image-record
  (s/keys :req-un [:image-record/id
                   :image-record/url
                   :image-record/image_type]))
(s/fdef record->image
  :args (s/cat :record ::image-record)
  :ret (s/nilable ::eimage/image))
(defn- record->image
  [record]
  (when (#{1 2} (:image_type record))
    (-> record
        (set/rename-keys {:image_type :type})
        (update :type #(case %
                         1 :main
                         2 :sub
                         nil)))))

(s/fdef update-user-image
  :args (s/cat :c ::user-repository
               :user-id ::euser/id
               :image-ids (s/coll-of ::eimage/id))
  :ret ::euser/images)
(defn- update-user-image
  [{:keys [mysql-datasource] :as c}
   user-id
   image-ids]
  (let [last-images (->> (user_image-by-user_id (:db mysql-datasource)
                                                {:user_id user-id})
                         (map record->image)
                         (filter #(not (nil? %))))]
    (jdbc/with-db-transaction [tx (:db mysql-datasource)]
      (if (seq last-images)
        (do
          (delete-user_image
           tx
           {:image_id (->> last-images first :id)
            :user_id user-id})
          (insert-user_image
           tx
           {:image_id (-> image-ids first)
            :user_id user-id
            :image_type 1}))
        (insert-user_image
         tx
         {:image_id (-> image-ids first)
          :user_id user-id
          :image_type 1})))))

(s/def :update-user-params/image-ids (s/coll-of ::eimage/id))
(s/fdef update-user
  :args (s/cat :c (s/keys :req-un [::mysql-datasource])
               :params (s/keys :req-un [::euser/id]
                               :opt-un [::euser/name
                                        ::euser/introduction
                                        :update-user-params/image-ids]))
  :ret nil?)
(defn update-user
  [{:keys [mysql-datasource] :as c}
   {:keys [image-ids] :as params}]
  (when (seq image-ids)
    (update-user-image c (:id params) image-ids))
  (when (seq (->> (keys params) (remove #{:id :image-ids})))
    (update-user-by-id
     (:db mysql-datasource)
     (select-keys params [:id :name :introduction])))
  nil)

(s/fdef get-user-id-by-session
  :args (s/cat :c (s/keys :req-un [::firebase-admin-datasource])
               :session ::euser/session_cookie)
  :ret ::euser/id)
(defn get-user-id-by-session
  [{:keys [firebase-admin-datasource
           mysql-datasource]}
   session]
  (try (-> (user-by-firebase_id
            (:db mysql-datasource)
            {:firebase_id
             (-> firebase-admin-datasource
                 :auth
                 (.verifySessionCookie session true)
                 .getUid)})
           :id)
       (catch Exception e
         (throw (ex-info "無効なセッションです"
                         {:type :invalid-session})))))

(defrecord UserRepositoryComponent [mysql-datasource
                                    firebase-admin-datasource]
  component/Lifecycle
  (start [this]
    this)
  (stop [this]
    this))
