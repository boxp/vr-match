(ns vr-match.mypage.container
  (:require
   [clojure.string :as string]
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [vr-match.util :as util]
   [vr-match.events :as events]
   [vr-match.subs :as subs]
   [vr-match.mypage.events :as mypage-events]
   [vr-match.mypage.subs :as mypage-subs]
   [vr-match.mypage.component :as component]))

(defn handle-submit-user-name
  [user-name]
  (re-frame/dispatch
   [::mypage-events/update-me {:name user-name}]))

(defn handle-submit-introduction
  [introduction]
  (re-frame/dispatch
   [::mypage-events/update-me {:introduction introduction}]))

(defn handle-submit-main-image
  [main-image-data-url]
  (re-frame/dispatch
   [::mypage-events/upload-image
    {:base64-string (->> (string/split main-image-data-url
                                       #",")
                         last)}]))

(defn handle-initialize []
  (re-frame/dispatch
   [::events/fetch-me true]))

(def mypage-state
  (r/atom {:platformOptions [{:id 1 :name "VRChat" :exampleUserId "usr_3b6403c3-be9f-432c-ab1f-446778946421"}
                             {:id 2 :name "YouTube" :exampleUserId "BOXPKETARO"}
                             {:id 3 :name "VirtualCast" :exampleUserId "6265398"}]}))

(defn mypage
  [_]
  (let [isLoading (re-frame/subscribe [::mypage-subs/loading?])
        me (re-frame/subscribe [::subs/me])]
    (fn [props]
      [component/mypage (merge @mypage-state
                               {:me @me
                                :isLoading @isLoading
                                :handleInitialize handle-initialize
                                :handleSubmitUserName handle-submit-user-name
                                :handleSubmitIntroduction handle-submit-introduction
                                :handleSubmitMainImage handle-submit-main-image})])))

(util/universal-set-loaded! :mypage)
