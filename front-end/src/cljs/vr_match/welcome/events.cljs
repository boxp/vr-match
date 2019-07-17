(ns vr-match.welcome.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.coeffects :as coeffects]
   [vr-match.events :as events]))

(re-frame/reg-event-fx
 ::initialize
 [(re-frame/inject-cofx ::coeffects/local-store "session")]
 (fn [{:keys [db local-store]} _]
   (when (seq local-store)
     {:dispatch [::events/push "/search"]})))
