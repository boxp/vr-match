(ns vr-match.auth.containers.email-register
  (:require [re-frame.core :as re-frame]
            [vr-match.auth.components.email-register :as components]
            [vr-match.auth.events :as events]
            [vr-match.util :as util]))

(defn- handle-initialize
  []
  (re-frame/dispatch [::events/initialize]))

(defn- handle-submit
  [email]
  (re-frame/dispatch [::events/send-sign-in-link-to-email {:email email
                                                           :callback-path "/email-register"}]))

(defn email-register
  [_]
  (fn [props]
    [components/email-register {:handleInitialize handle-initialize
                                :handleSubmit handle-submit}]))

(util/universal-set-loaded! :email-register)
