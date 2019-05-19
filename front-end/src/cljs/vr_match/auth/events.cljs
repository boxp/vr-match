(ns vr-match.auth.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.auth.effects :as effects]))

(re-frame/reg-event-fx
 ::initialize
 (fn [{:keys [db]} _]
   {::effects/initialize-firebase {}}))
