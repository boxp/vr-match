(ns vr-match.auth.containers.login
(:require [reagent.core :as r]
          [re-frame.core :as re-frame]
          [vr-match.auth.components.login :as component]
          [vr-match.auth.events :as auth-events]
          [vr-match.util :as util]
          [vr-match.events :as events]))

;; TODO: re-frameつなぎ込み
(def login-state (r/atom {:backgroundImage "https://storage.googleapis.com/boxp-tmp/profile_sample.jpg"}))

(defn handle-click-twitter []
  (re-frame/dispatch [::events/push "/approach"]))

(defn handle-click-register []
  (re-frame/dispatch [::events/push "/register"]))

(defn handle-initialize []
  (re-frame/dispatch [::auth-events/initialize]))

(defn login
  [params]
  [component/login
   (merge @login-state
          {:handleClickTwitter handle-click-twitter
           :handleClickRegister handle-click-register
           :handleInitialize handle-initialize})])

(util/universal-set-loaded! :login)

