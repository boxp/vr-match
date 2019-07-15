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
    ;; TODO: meの取得
    }))

(re-frame/reg-event-fx
 ::on-error-update-me
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:db (-> db
            (assoc-in [:fetch-status :mypage] :loaded))
    :dispatch (case (-> errors first :extensions :type)
                "invalid-session" [::events/push "/"]
                [::events/api-error errors])}))

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
    :dispatch (case (-> errors first :extensions :type)
                "invalid-session" [::events/push "/"]
                [::events/api-error errors])}))

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
