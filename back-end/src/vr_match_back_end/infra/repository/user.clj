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
   [clj-time.spec :as t-spec]
   [com.stuartsierra.component :as component]
   [hugsql.core :refer [def-db-fns]]
   [cljstache.core :refer [render]]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.domain.entity.image :as eimage]
   [vr-match-back-end.domain.entity.platform :as eplatform]
   [vr-match-back-end.infra.datasource.firebase-admin :as firebase-admin]))

(def-db-fns "vr_match_back_end/infra/repository/sql/user.sql")
(def-db-fns "vr_match_back_end/infra/repository/sql/user_image.sql")
(def-db-fns "vr_match_back_end/infra/repository/sql/user_platform.sql")
(def-db-fns "vr_match_back_end/infra/repository/sql/user_skip.sql")
(def-db-fns "vr_match_back_end/infra/repository/sql/user_favorite.sql")

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
                                         (setExpiresIn (* 14 24 60 60 1000))
                                         build))))

(s/fdef create-new-user
  :args (s/cat :c ::user-repository
               :params (s/keys :req-un [::id-token]))
  :ret ::euser/user)
(defn create-new-user
  [{:keys [firebase-admin-datasource
           mysql-datasource] :as c}
   {:keys [id-token] :as params}]
  (jdbc/with-db-transaction [tx (:db mysql-datasource)]
    (let [firebase_id (get-firebase_id
                       firebase-admin-datasource
                       id-token)
          session_cookie (get-session_cookie firebase-admin-datasource
                                             id-token)
          [_ id] (insert-user tx
                              {:firebase_id firebase_id
                               :name ""
                               :introduction "はじめまして！"})]
      (-> (user-by-id tx
                      {:id id})
          (assoc :session_cookie session_cookie)))))

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
      (create-new-user c params))))


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

(s/fdef update-user-platforms
  :args (s/cat :c ::user-repository
               :user-id ::euser/id
               :platforms ::euser/platforms)
  :ret nil?)
(defn update-user-platforms
  [{:keys [mysql-datasource]}
   user-id
   platforms]
  (jdbc/with-db-transaction [tx (:db mysql-datasource)]
    (delete-user_platform-by-user_id
     tx
     {:user_id user-id})
    (when (seq platforms)
      (insert-user_platform-tuple
       tx
       {:platforms (->> platforms
                        (map (fn [{:keys [id platform-user-id]}]
                               [user-id id (or platform-user-id "")]))
                        vec)}))))

(s/def :update-user-params/image-ids (s/coll-of ::eimage/id))
(s/fdef update-user
  :args (s/cat :c (s/keys :req-un [::mysql-datasource])
               :params (s/keys :req-un [::euser/id]
                               :opt-un [::euser/name
                                        ::euser/introduction
                                        :update-user-params/image-ids
                                        ::euser/platforms]))
  :ret nil?)
(defn update-user
  [{:keys [mysql-datasource] :as c}
   {:keys [image-ids platforms] :as params}]
  (when (seq image-ids)
    (update-user-image c (:id params) image-ids))
  (when-not (nil? platforms)
    (update-user-platforms c (:id params) platforms))
  (when (seq (->> (keys params) (remove #{:id :image-ids :platforms})))
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

(s/fdef get-images-by-user-id
  :args (s/cat :c ::user-repository
               :user-id ::euser/id)
  :ret (s/coll-of ::eimage/image))
(defn- get-images-by-user-id
  [{:keys [mysql-datasource]}
   user-id]
  (->> (user_image-by-user_id (:db mysql-datasource)
                              {:user_id  user-id})
       (map record->image)))

(s/def :user-platform-record/id number?)
(s/def :user-platform-record/name string?)
(s/def :user-platform-record/url_template string?)
(s/def :user-platform-record/platform_user_id string?)
(s/def ::user-platform-record
  (s/keys :req-un [:user-platform-record/id
                   :user-platform-record/name
                   :user-platform-record/url_template
                   :user-platform-record/platform_user_id]))
(s/fdef record->platform
  :args (s/cat :record ::user-platform-record)
  :ret ::eplatform/platform)
(defn- record->platform
  [record]
  (cond-> record
    (= (:platform_user_id record) "")
    (-> (dissoc :url_template)
        (dissoc :platform_user_id))
    :always
    (-> (dissoc :url_template)
        (assoc :url (render (:url_template record)
                            {:user_id (:platform_user_id record)}))
        (set/rename-keys {:platform_user_id :platform-user-id}))))

(s/fdef get-platforms-by-user-id
  :args (s/cat :c ::user-repository
               :user-id ::euser/id)
  :ret (s/coll-of ::eplatform/platform))
(defn- get-platforms-by-user-id
  [{:keys [mysql-datasource]}
   user-id]
  (->> (user_platform-by-user_id (:db mysql-datasource) {:user_id user-id})
       (map record->platform)))

(s/fdef get-user-by-id
  :args (s/cat :c ::user-repository
               :id ::euser/id
               :with-images? boolean?
               :with-platforms? boolean?)
  :ret ::euser/user)
(defn get-user-by-id
  [{:keys [mysql-datasource] :as c}
   id
   with-images?
   with-platforms?]
  (cond-> (user-by-id (:db mysql-datasource) {:id id})
    with-images? (assoc :images (get-images-by-user-id c id))
    with-platforms? (assoc :platforms (get-platforms-by-user-id c id))
    :always identity))

(s/def :paging-parameters/offset number?)
(s/def :paging-parameters/limit number?)
(s/def ::paging-parameters
  (s/keys :opt-un [:paging-parameters/offset :paging-parameters/limit]))
(s/def :get-recommended-users-by-user-id-result/total number?)
(s/def :get-recommended-users-by-user-id-result/users (s/coll-of ::euser/user))
(s/def ::get-recommended-users-by-user-id-result
  (s/keys :req-un [:get-recommended-users-by-user-id-result/users]
          :opt-un [:get-recommended-users-by-user-id-result/total]))
(s/fdef get-recommended-users-by-user-id
  :args (s/cat :c ::user-repository
               :user-id ::euser/id
               :with-images? boolean?
               :with-platforms? boolean?
               :with-total? boolean?
               :paging-parameters ::paging-parameters
               :exclude-ids (s/coll-of ::euser/id))
  :ret (s/coll-of ::euser/user))
(defn get-recommended-users-by-user-id
  [{:keys [mysql-datasource] :as c}
   user-id
   with-images?
   with-platforms?
   with-total?
   {:keys [limit offset]}
   exclude-ids]
  (cond-> {}
    with-total? (assoc :total (:total
                               (count-recommended-user-by-user_id
                                (:db mysql-datasource)
                                {:user_id user-id
                                 :exclude_ids exclude-ids})))
    :always (assoc :users (->> (recommended-user-by-user_id
                                (:db mysql-datasource)
                                {:user_id user-id
                                 :offset (or offset 0)
                                 :limit (or limit 10)
                                 :exclude_ids exclude-ids})
                               (pmap #(cond-> %
                                        with-images? (assoc :images (get-images-by-user-id c (:id %)))
                                        with-platforms? (assoc :platforms (get-platforms-by-user-id c (:id %)))))))))

(s/fdef skip-partner
  :args (s/cat :c ::user-repository
               :me-id ::euser/id
               :partner-id ::euser/id)
  :ret nil?)
(defn skip-partner
  [{:keys [mysql-datasource]}
   me-id
   partner-id]
  (insert-user_skip (:db mysql-datasource)
                    {:from_id me-id
                     :to_id partner-id})
  nil)

(s/fdef favorite-partner
  :args (s/cat :c ::user-repository
               :me-id ::euser/id
               :partner-id ::euser/id))
(defn favorite-partner
  [{:keys [mysql-datasource]}
   me-id
   partner-id]
  (insert-user_favorite (:db mysql-datasource)
                        {:from_id me-id
                         :to_id partner-id})
  nil)

(s/def :get-favorited-users-from-user-id-paging-params/after (s/nilable ::t-spec/date-time))
(s/def :get-favorited-users-from-user-id-paging-params/first (s/nilable number?))
(s/def ::get-favorited-users-from-user-id-paging-params
  (s/keys :opt-un [:get-favorited-users-from-user-id-paging-params/after
                   :get-favorited-users-from-user-id-paging-params/first]))
(s/def :get-favorited-users-from-user-id-result/total number?)
(s/def :get-favorited-users-from-user-id-result/users (s/coll-of ::euser/user))
(s/def ::get-favorited-users-from-user-id-result
  (s/keys :req-un [:get-favorited-users-from-user-id-result/users]
          :opt-un [:get-favorited-users-from-user-id-result/total]))
(s/fdef get-favorited-users-from-user-id
  :args (s/cat :c ::user-repository
               :user-id ::euser/id
               :with-images? boolean?
               :with-platforms? boolean?
               :with-total? boolean?
               :paging-parameters ::get-favorited-users-from-user-id-paging-params)
  :ret (s/coll-of ::euser/user))
(defn get-favorited-users-from-user-id
  [{:keys [mysql-datasource] :as c}
   user-id
   with-images?
   with-platforms?
   with-total?
   {:keys [after first]}]
  (cond-> {}
    with-total? (assoc :total (:total
                               (count-favorited-user-by-user_id
                                (:db mysql-datasource)
                                {:user_id user-id})))
    :always (assoc :users (->> (favorited-user-by-user_id
                                (:db mysql-datasource)
                                {:user_id user-id
                                 :after (or after (t/now))
                                 :limit (or first 1000)})
                               (pmap #(cond-> %
                                        with-images? (assoc :images (get-images-by-user-id c (:id %)))
                                        with-platforms? (assoc :platforms (get-platforms-by-user-id c (:id %)))))))))

(s/fdef get-user-matched?
  :args (s/cat :c ::user-repository
               :me-id ::euser/id
               :partner-id ::euser/id))
(defn get-user-matched?
  [{:keys [mysql-datasource]}
   me-id
   partner-id]
  (<= 2
      (-> (count-user_favorite-by-each-other-id
           (:db mysql-datasource)
           {:me_id me-id
            :partner_id partner-id})
          :total)))

(s/def :get-matched-users-by-user-id-paging-params/after (s/nilable ::t-spec/date-time))
(s/def :get-matched-users-by-user-id-paging-params/first (s/nilable number?))
(s/def ::get-matched-users-by-user-id-paging-params
  (s/keys :opt-un [:get-matched-users-by-user-id-paging-params/after
                   :get-matched-users-by-user-id-paging-params/first]))
(s/def :get-matched-users-by-user-id-result/users (s/coll-of ::euser/user))
(s/def :get-matched-users-by-user-id-result/has-next? boolean?)
(s/def ::get-matched-users-by-user-id-result
  (s/keys :req-un [:get-matched-users-by-user-id-result/users]
          :opt-un [:get-matched-users-by-user-id-result/has-next?]))
(s/fdef get-matched-users-by-user-id
  :args (s/cat :c ::user-repository
               :user-id ::euser/id
               :with-images? boolean?
               :with-platforms? boolean?
               :with-has-next? boolean?
               :paging-params ::get-matched-users-by-user-id-paging-params)
  :ret ::get-matched-users-by-user-id-result)
(defn get-matched-users-by-user-id
  [{:keys [mysql-datasource] :as c}
   user-id
   with-images?
   with-platforms?
   with-has-next?
   paging-params]
  (if with-has-next?
    (let [limit (inc (or (:first paging-params) 1000))
          users (matched-user-by-user_id
                      (:db mysql-datasource)
                      {:user_id user-id
                       :after (or (:after paging-params) (t/now))
                       :limit limit})
          has-next? (>= (count users) limit)]
      {:users (->> (if has-next? (drop-last users) users)
                   (pmap #(cond-> %
                            with-images? (assoc :images (get-images-by-user-id c (:id %)))
                            with-platforms? (assoc :platforms (get-platforms-by-user-id c (:id %))))))
       :has-next? has-next?})
    {:users (->> (matched-user-by-user_id
                  (:db mysql-datasource)
                  {:user_id user-id
                   :after (or (:after paging-params) (t/now))
                   :limit (or (:first paging-params) 1000)})
                  (pmap #(cond-> %
                           with-images? (assoc :images (get-images-by-user-id c (:id %)))
                           with-platforms? (assoc :platforms (get-platforms-by-user-id c (:id %))))))}))

(s/fdef get-user
  :args (s/cat :c ::user-repository
               :partner-id ::euser/id
               :with-images? boolean?
               :with-platforms? boolean?
               :me-id (s/nilable ::euser/id))
  :ret ::euser/user)
(defn get-user
  ([{:keys [mysql-datasource] :as c}
    partner-id
    with-images?
    with-platforms?]
   (cond-> (user-by-id (:db mysql-datasource)
                       {:id partner-id})
     with-images? (assoc :images (get-images-by-user-id c partner-id))
     with-platforms? (assoc :platforms (get-platforms-by-user-id c partner-id))))
  ([{:keys [mysql-datasource] :as c}
    partner-id
    with-images?
    with-platforms?
    me-id]
   (cond-> (user-with-is_matched (:db mysql-datasource)
                                 {:partner_id partner-id
                                  :me_id me-id})
     with-images? (assoc :images (get-images-by-user-id c partner-id))
     with-platforms? (assoc :platforms (get-platforms-by-user-id c partner-id))
     :always (-> (set/rename-keys {:is_matched :matched?})
                 (update :matched? #(= % 1))))))

(s/fdef delete-all-skip-from-user
  :args (s/cat :c ::user-repository
               :user-id ::euser/id)
  :ret nil?)
(defn delete-all-skip-from-user
  [{:keys [mysql-datasource] :as c}
   user-id]
  (delete-all-user_skip-by-user_id (:db mysql-datasource)
                                   {:user_id user-id})
  nil)

(defrecord UserRepositoryComponent [mysql-datasource
                                    firebase-admin-datasource]
  component/Lifecycle
  (start [this]
    this)
  (stop [this]
    this))
