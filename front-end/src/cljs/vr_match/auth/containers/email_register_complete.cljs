(ns vr-match.auth.containers.email-register-complete
  (:require
   [re-frame.core :as re-frame]
   [vr-match.util :as util]
   [vr-match.events :as events]
   [vr-match.auth.events :as auth-events]
   [vr-match.auth.subs :as auth-subs]
   [vr-match.auth.components.email-register-complete :as component]))

(defn- handle-initialize
  []
  (re-frame/dispatch-sync [::auth-events/initialize])
  (re-frame/dispatch [::auth-events/auto-sign-in-with-email]))

(defn- handle-submit-email
  [email]
  (re-frame/dispatch [::auth-events/sign-in-with-email {:email email}]))

(defn email-register-complete
  [params]
  (let [loading? (re-frame/subscribe [::auth-subs/loading?])]
    (fn [_]
      [component/email-register-complete {:isLoading @loading?
                                          :handleInitialize handle-initialize
                                          :handleSubmitEmail handle-submit-email}])))

(util/universal-set-loaded! :email-register-complete)
