(ns vr-match.welcome.container
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [vr-match.events :as events]
            [vr-match.util :as util]
            [vr-match.welcome.events :as welcome-events]
            [vr-match.welcome.component :as component]))

;; TODO: re-frameつなぎ込み
(def welcome-state (r/atom {:backgroundImage "https://storage.googleapis.com/boxp-tmp/profile_sample.jpg"}))

(defn- handle-click-login-with-twitter []
  (re-frame/dispatch [::events/push "/approach"]))

(defn- handle-click-login-with-email []
  (re-frame/dispatch [::events/push "/email-login"]))

(defn- handle-click-register []
  (re-frame/dispatch [::events/push "/register"]))

(defn- handle-initialize []
  (re-frame/dispatch [::welcome-events/initialize]))

(defn welcome
  [params]
  [component/welcome
   (merge @welcome-state
          {:handleClickLoginWithTwitter handle-click-login-with-twitter
           :handleClickLoginWithEmail handle-click-login-with-email
           :handleClickRegister handle-click-register
           :handleInitialize handle-initialize})])

(util/universal-set-loaded! :welcome)
