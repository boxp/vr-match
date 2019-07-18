(ns vr-match.auth.containers.email-register
  (:require [re-frame.core :as re-frame]
            [vr-match.auth.components.email-register :as components]
            [vr-match.auth.subs :as auth-subs]
            [vr-match.auth.events :as events]
            [vr-match.util :as util]))

(defn- handle-initialize
  []
  (re-frame/dispatch [::events/initialize]))

(defn- handle-submit
  [email]
  (re-frame/dispatch [::events/send-sign-in-link-to-email {:email email
                                                           :redirect-path "/email-register-complete"}]))

(defn email-register
  [_]
  (let [loading? (re-frame/subscribe [::auth-subs/loading?])
        is-completed-send-link (re-frame/subscribe [::auth-subs/send-sign-in-link-to-email-succeed?])
        sent-email (re-frame/subscribe [::auth-subs/get-sent-email])]
    (fn [props]
      [components/email-register {:isCompletedSendLink @is-completed-send-link
                                  :isLoading @loading?
                                  :sentEmail @sent-email
                                  :handleInitialize handle-initialize
                                  :handleSubmit handle-submit}])))

(util/universal-set-loaded! :email-register)
