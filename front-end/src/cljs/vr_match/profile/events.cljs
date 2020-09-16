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
                                     [:images [:id :url]]
                                     [:platforms [:id :name :url :platformUserId]]]]]}
                  :success-handler ::on-success-fetch-partner
                  :error-handler ::on-error-fetch-partner}]})))

