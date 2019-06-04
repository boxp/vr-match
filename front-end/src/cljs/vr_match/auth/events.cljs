(ns vr-match.auth.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.effects :as effects]
   [vr-match.coeffects :as coeffects]
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
            (assoc-in [:api-error] error)
            (assoc-in [:auth :sign-in-link :error] error)
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

(re-frame/reg-event-fx
 ::success-sign-in-with-email
 (fn [{:keys [db]}
      [_ email]]
   {::effects/remove-localstorage {:key "emailForSignIn"}
    :db (-> db
            (assoc-in [:fetch-status :sign-in-with-email] :loaded)
            (assoc-in [:auth :sign-in-link :error] nil))}))

(re-frame/reg-event-fx
 ::error-sign-in-with-email
 (fn [{:keys [db]}
      [_ error]]
   {:db (-> db
            (assoc-in [:api-error] error)
            (assoc-in [:auth :sign-in-with-email :error] error)
            (assoc-in [:fetch-status :sign-in-link] :loaded))}))

(re-frame/reg-event-fx
 ::sign-in-with-email
 (fn [{:keys [db]}
      [_ {:keys [email]}]]
   (when (-> db :fetch-status :sign-in-with-email (not= :loading))
     {::auth-effects/sign-in-with-email-link {:email email
                                              :callback-success [::success-sign-in-with-email]
                                              :callback-error [::error-sign-in-with-email]}
      :db (-> db
              (assoc-in [:fetch-status :sign-in-with-email] :loading))})))

(re-frame/reg-event-fx
 ::auto-sign-in-with-email
 [(re-frame/inject-cofx ::coeffects/local-store "emailForSignIn")]
 (fn [{:keys [db local-store]}
      _]
   (if local-store
     {:dispatch [::sign-in-with-email {:email local-store}]}
     {:db (-> db
              (assoc-in [:auth :sign-in-with-email :email-input-required?] true))})))
