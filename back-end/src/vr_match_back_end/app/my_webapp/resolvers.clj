(ns vr-match-back-end.app.my-webapp.resolvers
  (:require
   [com.stuartsierra.component :as component]
   [clojure.data.codec.base64 :as b64]
   [clojure.stacktrace :refer [print-stack-trace]]
   [clojure.set :as set]
   [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
   [vr-match-back-end.app.my-webapp.converter :refer [user->User]]
   [vr-match-back-end.domain.usecase.auth :as uauth]
   [vr-match-back-end.domain.usecase.image :as uimage]
   [vr-match-back-end.domain.usecase.user :as uuser]))

(defmulti handle-error #(some-> % ex-data :type))

(defmethod handle-error :invalid-session [error]
  (resolve-as nil
              {:message (.getMessage error)
               :type (-> error ex-data :type)}))

(defmethod handle-error :default [error]
  (print-stack-trace error)
  (resolve-as nil {:message "Something has wrong."}))

(defn approach-list
  [context arguments value]
  (try
    (let [{:keys [first after last before]} arguments
          cursor (-> 1 str .getBytes b64/encode)]
      {:edges
       (->>
        [{:node
          {:id 1
           :title "サンプル画像"
           :name "一箱"
           :introduction "バーチャル清楚系女子高校生Webアプリケーションエンジニアおじさんです。こっそりプログラミングしてます。"
           :platforms [{:id 1 :name "VRChat"} {:id 2 :name "VRoidHub"} {:id 3 :name "VirtualCast"}]
           :images ["https://storage.googleapis.com/boxp-tmp/profile_sample.png"]}
          :cursor (-> 1 str .getBytes b64/encode)}
         {:node
          {:id 2
           :title "サンプル画像"
           :name "ヒマリ"
           :introduction "一箱さんちのヒマリです！"
           :platforms [{:id 1 :name "VRChat"} {:id 3 :name "VirtualCast"}]
           :images ["https://storage.googleapis.com/boxp-tmp/profile_sample_2.jpg"]}
          :cursor (-> 2 str .getBytes b64/encode)}
         {:node
          {:id 3
           :title "サンプル画像"
           :name "アリシア・ソリッド"
           :introduction "ニコニ立体で公式キャラクターやってます。よろしくお願いします！"
           :platforms [{:id 3 :name "VirtualCast"}]
           :images ["https://storage.googleapis.com/boxp-tmp/profile_sample_3.jpg"]}
          :cursor (-> 3 str .getBytes b64/encode)}]
        cycle
        (take first))
       :pageInfo {:startCursor cursor
                  :endCursor cursor
                  :hasPreviousPage true
                  :hasNextPage true}
       :total 99999})
    (catch Exception e (handle-error e))))

(defn register-user
  [{:keys [auth-usecase] :as context}
   {:keys [idToken] :as arguments}
   value]
  (try
    (let [user (uauth/register auth-usecase idToken)]
      {:user (-> user
                 user->User
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
                 user->User
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
           imageIds] :as params}]
  (try
    (do
      (uuser/update-me
       user-usecase
       session
       (set/rename-keys params {:imageIds :image-ids}))
      true)
    (catch Exception e (handle-error e))))

(defrecord MyWebappResolversComponent [auth-usecase image-usecase user-usecase]
  component/Lifecycle
  (start [this]
    (-> this))
  (stop [this]
    (-> this)))
