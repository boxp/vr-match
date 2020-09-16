(ns vr-match.profile.events
  (:require [re-frame.core :as re-frame]
            [vr-match.events :as events]))

(re-frame/reg-event-fx
 ::on-success-fetch-partner
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:fetch-status :profile] :loaded)
            (assoc-in [:profile :partner] (-> data :partner)))}))

(re-frame/reg-event-fx
 ::on-error-fetch-partner
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:db (assoc-in db [:fetch-status :profile] :loaded)
    :dispatch (case (-> errors first :extensions :type)
                "invalid-session" [::push "/"]
                [::events/api-error errors])}))

(re-frame/reg-event-fx
 ::fetch-partner
 (fn [{:keys [db]}
      [_ {:keys [id]}]]
   (when (not= (-> db :fetch-status :profile) :loading)
     {:db (-> db
              (assoc-in [:fetch-status :profile] :loading))
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/queries [[:partner {:id id}
                                    [:id
                                     :name
                                     :introduction
                                     :isMatched
                                     :isFavorited
                                     [:images [:id :url]]
                                     [:platforms [:id :name :url :platformUserId]]]]]}
                  :success-handler ::on-success-fetch-partner
                  :error-handler ::on-error-fetch-partner}]})))

(re-frame/reg-event-fx
 ::on-success-favorite
 (fn [{:keys [db]} [_ {:keys [data]}]]
   (let [partner-id (-> data :profile :partner :id)]
     {:db (-> db
              (assoc-in [:fetch-status :profile] :loaded)
              (assoc-in [:profile :show-matching-dialog] (-> data :favorite :isMatched))
              (assoc-in [:profile :partner :isMatched] (-> data :favorite :isMatched))
              (assoc-in [:profile :partner :isFavorited] true)
              (update-in [:approach :list] #(remove (fn [user] (= (:id user) partner-id)) %)))})))

(re-frame/reg-event-fx
 ::on-error-favorite
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:db (assoc-in db [:fetch-status :profile] :loaded)
    :dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::favorite
 (fn [{:keys [db] :as cofx} [_ {:keys [id] :as user}]]
   (when (not= (-> db :fetch-status :profile) :loading)
     {:db (-> db
              (assoc-in [:fetch-status :profile] :loading))
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/operation {:operation/type :mutation
                                     :operation/name "favorite"}
                   :venia/queries [[:favorite {:partnerId id}
                                    [:isMatched]]]}
                  :success-handler ::on-success-favorite
                  :error-handler ::on-error-favorite}]})))

(re-frame/reg-event-fx
 ::close-matching-dialog
 (fn [{:keys [db]}]
   {:db (-> db
            (assoc-in [:profile :show-matching-dialog] false))}))
