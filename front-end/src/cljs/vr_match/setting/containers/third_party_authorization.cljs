(ns vr-match.setting.containers.third-party-authorization
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [vr-match.auth.events :as auth-events]
            [vr-match.setting.components.third-party-authorization :as components]
            [vr-match.util :as util]))

(def fake-state
  (r/atom {:isTwitterEnabled false}))

(defn- handle-initialize []
  (re-frame/dispatch-sync [::auth-events/initialize])
  (re-frame/dispatch [::auth-events/fetch-linked-provider-ids]))

(defn- handle-change-twitter []
  (swap! fake-state #(update % :isTwitterEnabled not)))

(defn third-party-authorization []
  [components/third-party-authorization {:isLoading false
                                         :isTwitterEnabled (:isTwitterEnabled @fake-state)
                                         :handleInitialize handle-initialize
                                         :handleChangeTwitter handle-change-twitter}])

(util/universal-set-loaded! :setting-third-party-authorization)
