(ns vr-match.setting.containers.third-party-authorization
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [vr-match.events :as events]
            [vr-match.auth.events :as auth-events]
            [vr-match.auth.subs :as auth-subs]
            [vr-match.setting.subs :as setting-subs]
            [vr-match.setting.components.third-party-authorization :as components]
            [vr-match.util :as util]))

(def fake-state
  (r/atom {:isTwitterEnabled false}))

(defn- handle-initialize []
  (re-frame/dispatch [::events/fetch-me])
  (re-frame/dispatch-sync [::auth-events/initialize])
  (re-frame/dispatch [::auth-events/fetch-linked-provider-ids]))

(defn- handle-link-twitter []
  (re-frame/dispatch [::auth-events/link-with-twitter]))

(defn- handle-unlink-twitter []
  (re-frame/dispatch [::auth-events/unlink-with-twitter]))

(defn- handle-unlink-email []
  (re-frame/dispatch [::auth-events/unlink-with-email]))

(defn third-party-authorization []
  (let [twitter-enabled? (re-frame/subscribe [::auth-subs/linked-twitter?])
        email-enabled? (re-frame/subscribe [::auth-subs/linked-email?])
        loading? (re-frame/subscribe [::setting-subs/third-party-authorization-loading?])
        canUnlink (re-frame/subscribe [::setting-subs/can-unlink?])]
    [components/third-party-authorization {:isLoading @loading?
                                           :isTwitterEnabled @twitter-enabled?
                                           :isEmailEnabled @email-enabled?
                                           :canUnlink @canUnlink
                                           :handleInitialize handle-initialize
                                           :handleLinkTwitter handle-link-twitter
                                           :handleUnlinkTwitter handle-unlink-twitter
                                           :handleUnlinkEmail handle-unlink-email}]))

(util/universal-set-loaded! :setting-third-party-authorization)
