(ns vr-match.profile.container
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [vr-match.profile.component :as component]
            [vr-match.profile.events :as profile-events]
            [vr-match.profile.subs :as profile-subs]
            [vr-match.util :as util]
            [vr-match.events :as events]
            [vr-match.subs :as subs]))

(defn profile
  [params]
  (let [id (-> params :id (js/parseInt 10))
        me (re-frame/subscribe [::subs/me])
        partner (re-frame/subscribe [::profile-subs/partner])
        isLoading (re-frame/subscribe [::profile-subs/loading?])
        isShowMatchingDialog (re-frame/subscribe [::profile-subs/show-matching-dialog?])
        handleInitialize (fn []
                           (re-frame/dispatch [::profile-events/fetch-partner {:id id}])
                           (re-frame/dispatch [::events/fetch-me {:with-images? true
                                                                  :with-platforms? true}]))
        handleClickFavorite (fn [] (re-frame/dispatch [::profile-events/favorite @partner]))
        handleCloseMatchingDialog (fn [] (re-frame/dispatch [::profile-events/close-matching-dialog]))]
    (fn [params]
      [component/profile {:me @me
                          :partner @partner
                          :isLoading @isLoading
                          :isShowMatchingDialog @isShowMatchingDialog
                          :handleInitialize handleInitialize
                          :handleClickFavorite handleClickFavorite
                          :handleCloseMatchingDialog handleCloseMatchingDialog}])))

(util/universal-set-loaded! :profile)
