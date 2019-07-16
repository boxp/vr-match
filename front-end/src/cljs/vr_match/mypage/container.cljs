(ns vr-match.mypage.container
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [vr-match.util :as util]
   [vr-match.mypage.events :as events]
   [vr-match.mypage.subs :as subs]
   [vr-match.mypage.component :as component]))

(defn handle-submit-user-name
  [user-name]
  (re-frame/dispatch
   [::events/update-me {:name user-name}]))

(defn handle-submit-introduction
  [introduction]
  (re-frame/dispatch
   [::events/update-me {:introduction introduction}]))

(defn handle-submit-main-image
  [main-image-data-url]
  (re-frame/dispatch
   [::events/upload-image
    {:base64-string (->> (string/split main-image-data-url
                                       #",")
                         last)}]))

(def mypage-state
  (r/atom {:me
           {:id 1
            :title "サンプル画像"
            :userName "一箱"
            :introduction "バーチャル清楚系女子高校生Webアプリケーションエンジニアおじさんです。こっそりプログラミングしてます。"
            :platForms [{:id 1 :name "VRChat" :link "https://vrchat.net/home/user/usr_3b6403c3-be9f-432c-ab1f-446778946421" :userId "usr_3b6403c3-be9f-432c-ab1f-446778946421"}
                        {:id 2 :name "YouTube" :link "https://www.youtube.com/user/BOXPKETARO/about" :userId "BOXPKETARO"}
                        {:id 3 :name "VirtualCast" :link ""}]
            :image ["https://storage.googleapis.com/boxp-tmp/profile_sample.png"]
            :isMatched false}
           :platformOptions [{:id 1 :name "VRChat" :exampleUserId "usr_3b6403c3-be9f-432c-ab1f-446778946421"}
                             {:id 2 :name "YouTube" :exampleUserId "BOXPKETARO"}
                             {:id 3 :name "VirtualCast" :exampleUserId "6265398"}]}))

(defn mypage
  [_]
  (let [isLoading (re-frame/subscribe [::subs/loading?])]
    (fn [props]
      [component/mypage (merge @mypage-state
                               {:isLoading @isLoading
                                :handleSubmitUserName handle-submit-user-name
                                :handleSubmitIntroduction handle-submit-introduction
                                :handleSubmitMainImage handle-submit-main-image})])))

(util/universal-set-loaded! :mypage)
