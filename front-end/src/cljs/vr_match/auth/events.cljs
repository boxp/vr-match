(ns vr-match.auth.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.effects :as effects]
   [vr-match.auth.effects :as auth-effects]))

(re-frame/reg-event-fx
 ::initialize
 (fn [{:keys [db]} _]
   {::auth-effects/initialize-firebase {}}))

(re-frame/reg-event-fx
 ::success-send-sign-in-link-to-email
 (fn [{:keys [db]}
      [_ email]]
   {::effects/set-localstorage {:key "emailForSignIn"
                                :item email}
    :db (-> db
            (assoc-in [:fetch-status :sign-in-link] :loaded)
            (assoc-in [:auth :sign-in-link :error] nil)
            (assoc-in [:auth :sign-in-link :email] email))}))

(re-frame/reg-event-fx
 ::error-send-sign-in-link-to-email
 (fn [{:keys [db]}
      [_ error]]
   {:db (-> db
            (assoc :api-error error)
            (assoc-in [:fetch-status :sign-in-link] :loaded))}))

(re-frame/reg-event-fx
 ::send-sign-in-link-to-email
 (fn [{:keys [db]}
      [_ {:keys [email redirect-path]}]]
   (when (-> db :fetch-status :sign-in-link (not= :loading))
     {::auth-effects/send-sign-in-link-to-email {:email email
                                                 :redirect-path redirect-path
                                                 :callback-success [::success-send-sign-in-link-to-email]
                                                 :callback-error [::error-send-sign-in-link-to-email]}
      :db (-> db
              (assoc-in [:fetch-status :sign-in-link] :loading))})))
