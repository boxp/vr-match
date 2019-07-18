(ns vr-match.mypage.events
  (:require
   [cljs.spec.alpha :as s]
   [re-frame.core :as re-frame]
   [vr-match.events :as events]))

(re-frame/reg-event-fx
 ::on-success-update-me
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (assoc-in db [:fetch-status :mypage] :loaded)
    :dispatch [::events/fetch-me {:with-images? true
                                  :with-platforms? true}]}))

(re-frame/reg-event-fx
 ::on-error-update-me
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:db (-> db
            (assoc-in [:fetch-status :mypage] :loaded))
    :dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::update-me
 (fn [{:keys [db]}
      [_ params]]
   (when (-> db :fetch-status :mypage (not= :loading))
     {:db (assoc-in db [:fetch-status :mypage] :loading)
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
            (assoc-in [:fetch-status :mypage] :loaded))
    :dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::on-success-upload-image
 (fn [{:keys [db]}
      [_ {:keys [data] :as payload}]]
   {:db (-> db
            (assoc-in [:fetch-status :mypage] :loaded)
            (assoc-in [:mypage :uploaded-image] (:uploadImage data)))
    :dispatch [::update-me
               {:imageIds
                [(-> data :uploadImage :id)]}]}))

(re-frame/reg-event-fx
 ::upload-image
 (fn [{:keys [db]}
      [_ {:keys [base64-string]}]]
   (when (-> db :fetch-status :mypage (not= :loading))
     {:db (assoc-in db [:fetch-status :mypage] :loading)
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
            (assoc-in [:mypage :platform-options] (-> data :platformOptions))
            (assoc-in [:fetch-status :mypage] :loaded))}))

(re-frame/reg-event-fx
 ::on-error-fetch-platform-options
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:db (-> db
            (assoc-in [:fetch-status :mypage] :loaded))
    :dispatch [::events/api-error errors]}))

(re-frame/reg-event-fx
 ::fetch-platform-options
 (fn [{:keys [db]} _]
   (when (-> db :fetch-status :mypage)
     {:db (assoc-in db [:fetch-status :mypage] :loading)
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/queries [[:platformOptions
                                    [:id
                                     :name
                                     :exampleUserId]]]}
                  :success-handler ::on-success-fetch-platform-options
                  :error-handler ::on-error-fetch-platform-options}]})))

(re-frame/reg-event-fx
 ::initialize
 (fn [_ _]
   {:dispatch-n [[::events/fetch-me {:with-images? true
                                     :with-platforms? true}]
                 [::fetch-platform-options]]}))
