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

(defn handle-submit-platforms
  [platforms]
  (re-frame/dispatch
   [::mypage-events/update-me {:platforms
                               {:platforms
                                (->> platforms
                                     (map #(-> %
                                               (dissoc :url))))}}]))

(defn handle-submit-main-image
  [main-image-data-url]
  (re-frame/dispatch
   [::mypage-events/upload-image
    {:base64-string (->> (string/split main-image-data-url
                                       #",")
                         last)}]))

(defn handle-initialize []
  (re-frame/dispatch
   [::mypage-events/initialize]))

(defn mypage
  [_]
  (let [isLoading (re-frame/subscribe [::mypage-subs/loading?])
        platformOptions (re-frame/subscribe [::mypage-subs/platform-options])
        me (re-frame/subscribe [::subs/me])]
    (fn [props]
      [component/mypage (merge {:me @me
                                :platformOptions @platformOptions
                                :isLoading @isLoading
                                :handleInitialize handle-initialize
                                :handleSubmitUserName handle-submit-user-name
                                :handleSubmitIntroduction handle-submit-introduction
                                :handleSubmitMainImage handle-submit-main-image
                                :handleSubmitPlatforms handle-submit-platforms})])))

(util/universal-set-loaded! :mypage)
