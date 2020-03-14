(ns vr-match.auth.containers.twitter-login
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [vr-match.auth.events :as auth-events]
            [vr-match.auth.subs :as auth-subs]
            [vr-match.auth.components.twitter-login :as components]
            [vr-match.util :as util]))

(defn- handle-initialize []
  (re-frame/dispatch-sync [::auth-events/initialize])
  (re-frame/dispatch [::auth-events/check-twitter-redirect-result]))

(defn- handle-click-login-twitter []
  (re-frame/dispatch [::auth-events/sign-in-with-twitter]))

(defn twitter-login []
  (let [loading? (re-frame/subscribe [::auth-subs/loading-twitter-login?])]
    (r/create-class
     {:display-name "twitter-login"
      :component-did-mount handle-initialize
      :reagent-render
      (fn [_]
        [components/twitter-login {:handleClickLoginTwitter handle-click-login-twitter
                                   :isLoading @loading?}])})))

(util/universal-set-loaded! :twitter-login)
