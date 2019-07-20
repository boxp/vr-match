(ns vr-match.profile.container
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [vr-match.profile.component :as component]
            [vr-match.profile.events :as profile-events]
            [vr-match.profile.subs :as profile-subs]
            [vr-match.util :as util]
            [vr-match.events :as events]))

(defn profile
  [params]
  (let [id (-> params :id (js/parseInt 10))
        partner (re-frame/subscribe [::profile-subs/partner])
        isLoading (re-frame/subscribe [::profile-subs/loading?])
        handleInitialize (fn []
                           (re-frame/dispatch [::profile-events/fetch-partner {:id id}])
                           (re-frame/dispatch [::events/fetch-me]))]
    (fn [params]
      [component/profile {:partner @partner
                          :isLoading @isLoading
                          :handleInitialize handleInitialize}])))

(util/universal-set-loaded! :profile)
