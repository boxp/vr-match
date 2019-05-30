(ns vr-match.auth.containers.register
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [vr-match.auth.components.register :as component]
            [vr-match.events :as events]
            [vr-match.util :as util]))

(def register-state (r/atom {:backgroundImage "https://storage.googleapis.com/boxp-tmp/profile_sample_2.jpg"}))

(defn- handle-click-twitter []
  (re-frame/dispatch [::events/push "/wizard"]))

(defn- handle-click-email-register []
  (re-frame/dispatch [::events/push "/email-register"]))

(defn register
  [params]
  [component/register (merge @register-state
                             {:handleClickEmailRegister handle-click-email-register
                              :handleClickTwitter handle-click-twitter})])

(util/universal-set-loaded! :register)
