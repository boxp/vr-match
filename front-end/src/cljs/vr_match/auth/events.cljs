(ns vr-match.auth.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.auth.effects :as effects]))

(re-frame/reg-event-fx
 ::initialize
 (fn [{:keys [db]} _]
   {::effects/initialize-firebase {}}))

(re-frame/reg-event-fx
 ::send-sign-in-link-to-email
 (fn [{:keys [db]}
      [_ {:keys [email callback-path]}]]
   {::effects/send-sign-in-link-to-email {:email email
                                          :callback-path callback-path}}))
