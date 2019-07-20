(ns vr-match.myprofile.container
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [vr-match.events :as events]
   [vr-match.subs :as subs]
   [vr-match.myprofile.component :as component]
   [vr-match.util :as util]))

(defn- handle-initialize []
  (re-frame/dispatch [::events/fetch-me {:with-images? true
                                         :with-platforms? true}]))

(defn- handle-click-edit-my-profile []
  (re-frame/dispatch [::events/push "/mypage"]))

(defn myprofile
  [params]
  (let [me (re-frame/subscribe [::subs/me])
        isLoading (re-frame/subscribe [::subs/loading-me?])]
    (fn [_]
      [component/myprofile {:isLoading @isLoading
                            :me @me
                            :handleInitialize handle-initialize
                            :handleClickEditMyProfile handle-click-edit-my-profile}])))

(util/universal-set-loaded! :myprofile)
