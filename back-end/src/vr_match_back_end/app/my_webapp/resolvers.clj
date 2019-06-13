(ns vr-match-back-end.app.my-webapp.resolvers
  (:require
   [com.stuartsierra.component :as component]
   [clojure.data.codec.base64 :as b64]
   [vr-match-back-end.app.my-webapp.converter :refer [user->User]]
   [vr-match-back-end.domain.usecase.auth :as uauth]))

(defn approach-list
  [context arguments value]
  (let [{:keys [first after last before]} arguments
        cursor (-> 1 str .getBytes b64/encode)]
    {:edges
     (->>
      [{:node
        {:id 1
         :title "サンプル画像"
         :userName "一箱"
         :introduction "バーチャル清楚系女子高校生Webアプリケーションエンジニアおじさんです。こっそりプログラミングしてます。"
         :platForms [{:id 1 :name "VRChat"} {:id 2 :name "VRoidHub"} {:id 3 :name "VirtualCast"}]
         :image ["https://storage.googleapis.com/boxp-tmp/profile_sample.png"]}
        :cursor (-> 1 str .getBytes b64/encode)}
       {:node
        {:id 2
         :title "サンプル画像"
         :userName "ヒマリ"
         :introduction "一箱さんちのヒマリです！"
         :platForms [{:id 1 :name "VRChat"} {:id 3 :name "VirtualCast"}]
         :image ["https://storage.googleapis.com/boxp-tmp/profile_sample_2.jpg"]}
        :cursor (-> 2 str .getBytes b64/encode)}
       {:node
        {:id 3
         :title "サンプル画像"
         :userName "アリシア・ソリッド"
         :introduction "ニコニ立体で公式キャラクターやってます。よろしくお願いします！"
         :platForms [{:id 3 :name "VirtualCast"}]
         :image ["https://storage.googleapis.com/boxp-tmp/profile_sample_3.jpg"]}
        :cursor (-> 3 str .getBytes b64/encode)}]
      cycle
      (take first))
     :pageInfo {:startCursor cursor
                :endCursor cursor
                :hasPreviousPage true
                :hasNextPage true}
     :total 99999}))

(defn register-user
  [{:keys [auth-usecase] :as context}
   {:keys [idToken] :as arguments}
   value]
  (-> (uauth/register auth-usecase idToken)
      user->User
      (assoc :platforms [])))

(defrecord MyWebappResolversComponent [auth-usecase]
  component/Lifecycle
  (start [this]
    (println ";; Starting MyWebappResolversComponent")
    (-> this))
  (stop [this]
    (println ";; Stopping MyWebappResolversComponent")
    (-> this)))
