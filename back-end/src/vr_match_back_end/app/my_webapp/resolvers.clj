(ns vr-match-back-end.app.my-webapp.resolvers
  (:require
   [clojure.spec.alpha :as s]
   [clojure.data.codec.base64 :as b64]
   [clojure.stacktrace :refer [print-stack-trace]]
   [clojure.set :as set]
   [com.stuartsierra.component :as component]
   [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
   [com.walmartlabs.lacinia.executor :as executor]
   [vr-match-back-end.app.my-webapp.converter :as converter]
   [vr-match-back-end.domain.usecase.auth :as uauth]
   [vr-match-back-end.domain.usecase.image :as uimage]
   [vr-match-back-end.domain.usecase.user :as uuser]
   [vr-match-back-end.domain.usecase.platform :as uplatform]
   [vr-match-back-end.domain.usecase.approach :as uapproach]))

(defmulti handle-error #(some-> % ex-data :type))

(defmethod handle-error :invalid-session [error]
  (resolve-as nil
              {:message (.getMessage error)
               :type (-> error ex-data :type)}))

(defmethod handle-error :unregistered-user [error]
  (resolve-as nil
              {:message (.getMessage error)
               :type (-> error ex-data :type)}))

(defmethod handle-error :default [error]
  (print-stack-trace error)
  (resolve-as nil {:message "Something has wrong."}))

(s/fdef int->cursor
  :args (s/cat :n int?)
  :ret string?)
(defn int->cursor
  [n]
  (-> n str .getBytes b64/encode String.))

(s/fdef cursor->int
  :args (s/cat :cursor string?)
  :ret int?)
(defn cursor->int
  [cursor]
  (-> cursor .getBytes b64/decode String. Integer.))

(s/def :paging-arguments/first number?)
(s/def :paging-arguments/after string?)
(s/def :paging-arguments/last number?)
(s/def :paging-arguments/before string?)
(s/def ::paging-arguments
  (s/keys :opt-un [:paging-arguments/first
                   :paging-arguments/after
                   :paging-arguments/last
                   :paging-arguments/before]))
(s/fdef paging-arguments->paging-params
  :args (s/cat :paging-arguments ::paging-arguments)
  :ret ::uuser/paging-params)
(defn- paging-arguments->paging-params
  [paging-arguments]
  (let [first (:first paging-arguments)
        after (some-> paging-arguments :after cursor->int)
        last (:last paging-arguments)
        before (some-> paging-arguments :before cursor->int)]
    (cond
      (or first after) (cond-> {}
                         after (assoc :offset after)
                         first (assoc :limit first))
      (and last before) {:offset (- before last)
                         :limit last}
      before {:limit before}
      :else {})))

(defn approach-list
  [{:keys [user-usecase session]}
   {:keys [excludeIds] :as arguments}
   value]
  (try
    (let [{:keys [offset limit]} (paging-arguments->paging-params arguments)
          {:keys [total users]} (uuser/get-my-recommended-users
                                 user-usecase
                                 session
                                 true
                                 true
                                 true
                                 (paging-arguments->paging-params arguments)
                                 (or excludeIds [-1]))
          edges (seq (map-indexed
                      (fn [idx user]
                        {:node user
                         :cursor (int->cursor (+ (or offset 0) idx 1))}) users))]
      {:edges edges
       :pageInfo {:startCursor (->> edges first :cursor)
                  :endCursor (->> edges last :cursor)
                  :hasPreviousPage (> (or offset 0) 0)
                  :hasNextPage (if (seq edges)
                                 (< (some->> edges last :cursor cursor->int) total)
                                 false)}})
    (catch Exception e (handle-error e))))

(defn register-user
  [{:keys [auth-usecase] :as context}
   {:keys [idToken] :as arguments}
   value]
  (try
    (let [user (uauth/register auth-usecase idToken)]
      {:user (-> user
                 converter/user->User
                 (assoc :platforms []))
       :session (:session_cookie user)})
    (catch Exception e (handle-error e))))

(defn login-user
  [{:keys [auth-usecase] :as context}
   {:keys [idToken] :as arguments}
   value]
  (try
    (let [user (uauth/login auth-usecase idToken)]
      {:user (-> user
                 converter/user->User
                 ;; TODO: platforms, imagesの取得
                 )
       :session (:session_cookie user)})
    (catch Exception e (handle-error e))))

(defn upload-image
  [{:keys [image-usecase session] :as context}
   {:keys [base64String] :as arguments}
   value]
  (try
    (-> (uimage/upload-image
         image-usecase
         {:session (or session "")
          :base64-string base64String}))
    (catch Exception e (handle-error e))))

(defn update-me
  [{:keys [user-usecase session] :as context}
   {:keys [name
           introduction
           imageIds
           platforms] :as params}
   value]
  (try
    (do
      (uuser/update-me
       user-usecase
       session
       (cond-> params
         (:platforms params) (update :platforms
                                     (fn [platforms]
                                       (->> (:platforms platforms)
                                            (map #(set/rename-keys % {:platformUserId :platform-user-id})))))
         :always (set/rename-keys {:imageIds :image-ids})))
      true)
    (catch Exception e (handle-error e))))

(defn me
  [{:keys [user-usecase session] :as context} _ _]
  (try
    (->
     (uuser/get-me
      user-usecase
      session
      (executor/selects-field? context :User/images)
      (executor/selects-field? context :User/platforms))
     (update :platforms #(map converter/platform->Platform %)))
    (catch Exception e (handle-error e))))

(defn skip
  [{:keys [approach-usecase session]}
   {:keys [partnerId]}
   _]
  (try
    (do
      (uapproach/skip
       approach-usecase
       session
       partnerId)
      true)
    (catch Exception e (handle-error e))))

(defn favorite
  [{:keys [approach-usecase session]}
   {:keys [partnerId]}
   _]
  (try
    (-> (uapproach/favorite
         approach-usecase
         session
         partnerId)
        (set/rename-keys {:matched? :isMatched}))
    (catch Exception e (handle-error e))))

(defn favorited-users
  [{:keys [user-usecase session]} arguments _]
  (try
    (let [paging-params {:after (some-> arguments :after converter/decode-cursor converter/string->date-time)
                         :first (:first arguments)}
          {:keys [users]} (uuser/get-favorited-users-from-me
                           user-usecase
                           session
                           true
                           true
                           false
                           paging-params)
          edges (map (fn [user]
                       {:node user
                        :cursor (-> user :created_at converter/date-time->string converter/encode-cursor)}) users)]
      {:edges edges
       :pageInfo {:startCursor (some->> edges first :cursor)
                  :endCursor (some->> edges last :cursor)
                  :hasPreviousPage false
                  ;; FIXME: (= (count edges) first)の時嘘になる可能性があるがひとまずとしてこの実装にしている
                  :hasNextPage (>= (count edges)
                                   (or (:first paging-params) 1000))}})
    (catch Exception e (handle-error e))))

(defn matched-users
  [{:keys [user-usecase session]} arguments _]
  (try
    (let [paging-params {:after (some-> arguments
                                        :after
                                        converter/decode-cursor
                                        converter/string->date-time)
                         :first (:first arguments)}
          {:keys [users has-next?]} (uuser/get-my-matched-users
                                     user-usecase
                                     session
                                     true
                                     true
                                     true
                                     paging-params)
          edges (map (fn [user]
                       {:node user
                        :cursor (-> user
                                    :created_at
                                    converter/date-time->string
                                    converter/encode-cursor)}) users)]
      {:edges edges
       :pageInfo {:startCursor (some->> edges first :cursor)
                  :endCursor (some->> edges last :cursor)
                  :hasPreviousPage false
                  :hasNextPage has-next?}})
    (catch Exception e (handle-error e))))

(defn partner
  [{:keys [user-usecase session] :as context}
   {:keys [id] :as arguments}
   _]
  (try
    (-> (uuser/get-partner
         user-usecase
         session
         id
         (executor/selects-field? context :User/images)
         (executor/selects-field? context :User/platforms)
         (executor/selects-field? context :User/isMatched))
        converter/user->User
        (update :platforms #(map converter/platform->Platform %)))
    (catch Exception e (handle-error e))))

(defn reset-all-skip
  [{:keys [approach-usecase session]} _ _]
  (try
    (do
      (uapproach/reset-all-skip
       approach-usecase
       session)
      true)
    (catch Exception e (handle-error e))))

(defn platform-options
  [{:keys [platform-usecase]} _ _]
  (try
    (->> (uplatform/get-platform-masters platform-usecase)
         (map #(set/rename-keys % {:example-platform-user-id :exampleUserId})))
    (catch Exception e (handle-error e))))

(defrecord MyWebappResolversComponent [auth-usecase image-usecase user-usecase approach-usecase]
  component/Lifecycle
  (start [this]
    (-> this))
  (stop [this]
    (-> this)))
