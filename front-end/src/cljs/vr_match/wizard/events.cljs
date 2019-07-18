(ns vr-match.wizard.events
  (:require
   [cljs.spec.alpha :as s]
   [re-frame.core :as re-frame]
   [vr-match.events :as events]))

(re-frame/reg-event-fx
 ::initialize
 (fn [{:keys [db]}]
   {:dispatch-n [[::fetch-platform-options]
                 [::events/fetch-me {:with-images? false
                                     :with-platforms? false}]]}))

(re-frame/reg-event-fx
 ::on-success-update-me
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (assoc-in db [:fetch-status :wizard] :loaded)
    :dispatch [::events/fetch-me {:with-images? true
                                  :with-platforms? true}]}))

(re-frame/reg-event-fx
 ::on-error-update-me
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:db (-> db
            (assoc-in [:fetch-status :wizard] :loaded))
    :dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::update-me
 (fn [{:keys [db]}
      [_ params]]
   (when (-> db :fetch-status :wizard (not= :loading))
     {:db (assoc-in db [:fetch-status :wizard] :loading)
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/operation {:operation/type :mutation
                                     :operation/name "updateMe"}
                   :venia/queries [[:updateMe params]]}
                  :success-handler ::on-success-update-me
                  :error-handler ::on-error-update-me}]})))

(re-frame/reg-event-fx
 ::on-error-upload-image
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:db (-> db
            (assoc-in [:fetch-status :wizard] :loaded))
    :dispatch (case (-> errors first :extensions :type)
                "invalid-session" [::events/push "/"]
                [::events/api-error errors])}))

(re-frame/reg-event-fx
 ::on-success-upload-image
 (fn [{:keys [db]}
      [_ {:keys [data] :as payload}]]
   {:db (-> db
            (assoc-in [:fetch-status :wizard] :loaded)
            (assoc-in [:wizard :uploaded-image] (:uploadImage data)))
    :dispatch [::update-me
               {:imageIds
                [(-> data :uploadImage :id)]}]}))

(re-frame/reg-event-fx
 ::upload-image
 (fn [{:keys [db]}
      [_ {:keys [base64-string]}]]
   (when (-> db :fetch-status :wizard (not= :loading))
     {:db (assoc-in db [:fetch-status :wizard] :loading)
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/operation {:operation/type :mutation
                                     :operation/name "uploadImage"}
                   :venia/queries [[:uploadImage {:base64String base64-string}
                                    [:id
                                     :url]]]}
                  :success-handler ::on-success-upload-image
                  :error-handler ::on-error-upload-image}]})))

(re-frame/reg-event-fx
 ::on-success-fetch-platform-options
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:wizard :platform-options] (-> data :platformOptions))
            (assoc-in [:fetch-status :wizard] :loaded))}))

(re-frame/reg-event-fx
 ::on-error-fetch-platform-options
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:db (-> db
            (assoc-in [:fetch-status :wizard] :loaded))
    :dispatch [::events/api-error errors]}))

(re-frame/reg-event-fx
 ::fetch-platform-options
 (fn [{:keys [db]} _]
   (when (-> db :fetch-status :wizard)
     {:db (assoc-in db [:fetch-status :wizard] :loading)
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/queries [[:platformOptions
                                    [:id
                                     :name]]]}
                  :success-handler ::on-success-fetch-platform-options
                  :error-handler ::on-error-fetch-platform-options}]})))
