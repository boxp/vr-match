(ns vr-match.auth.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.effects :as effects]
   [vr-match.events :as events]
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
 ::on-error-login-user
 (fn [{:keys [db]}
      [_ payload]]
   {:db (-> db
            (assoc-in [:fetch-status :login-user] :loaded))
    :dispatch [::events/api-error payload]}))

(re-frame/reg-event-fx
 ::on-success-login-user
 (fn [{:keys [db]}
      [_ {:keys [data errors] :as payload}]]
   {:db (-> db
            (assoc :me (-> data :loginUser :user))
            (assoc-in [:fetch-status :login-user] :loaded))
    :dispatch-n [[::events/set-session (-> data :loginUser :session)]
                 [::events/push "/approach"]]}))

(re-frame/reg-event-fx
 ::login-user
 (fn [{:keys [db]}
      [_ id-token]]
   {:db (assoc-in db [:fetch-status :login-user] :loading)
    :dispatch [::events/graphql-query
               {:query
                {:venia/operation {:operation/type :mutation
                                   :operation/name "loginUser"}
                 :venia/queries [[:loginUser {:idToken id-token}
                                  [:session
                                   [:user
                                    [:id
                                     :name
                                     :introduction
                                     [:images
                                      [:id
                                       :url]]
                                     [:platforms
                                      [:id
                                       :name
                                       :url]]]]]]]}
                :success-handler ::on-success-login-user
                :error-handler ::on-error-login-user}]}))

(re-frame/reg-event-fx
 ::on-error-register-user
 (fn [{:keys [db]}
      [_ payload]]
   {:db (-> db
            (assoc-in [:fetch-status :register-user] :loaded))
    :dispatch [::events/api-error payload]}))

(re-frame/reg-event-fx
 ::on-success-register-user
 (fn [{:keys [db]}
      [_ {:keys [data errors] :as payload}]]
   {:db (-> db
            (assoc :me (-> data :registerUser :user))
            (assoc-in [:fetch-status :register-user] :loaded))
    :dispatch-n [[::events/set-session (-> data :registerUser :session)]
                 [::events/push "/wizard"]]}))

(re-frame/reg-event-fx
 ::register-user
 (fn [{:keys [db]}
      [_ id-token]]
   {:db (assoc-in db [:fetch-status :register-user] :loading)
    :dispatch [::events/graphql-query
                {:query
                 {:venia/operation {:operation/type :mutation
                                    :operation/name "registerUser"}
                  :venia/queries [[:registerUser {:idToken id-token}
                                   [:session
                                    [:user
                                     [:id
                                      :name
                                      :introduction
                                      [:images
                                       [:id
                                        :url]]
                                      [:platforms
                                       [:id
                                        :name
                                        :url]]]]]]]}
                  :success-handler ::on-success-register-user
                  :error-handler ::events/api-error}]}))

(re-frame/reg-event-fx
 ::success-renew-id-token
 (fn [{:keys [db]}
      [_ is-new id-token]]
   {:dispatch (if is-new
                [::register-user id-token]
                [::login-user id-token])}))

(re-frame/reg-event-fx
 ::success-sign-in-with-email
 (fn [{:keys [db]}
      [_ email is-new]]
   {::effects/remove-localstorage {:key "emailForSignIn"}
    ::auth-effects/renew-id-token {:callback-success [::success-renew-id-token is-new]
                                   :callback-error [:error-sign-in-with-email]}
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
