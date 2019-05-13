(ns vr-match.approach.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.events :as events]))

(re-frame/reg-event-db
 ::on-success-fetch-approach-list
 (fn [db [_ {:keys [data errors] :as payload}]]
   (assoc-in db [:approach :list] (-> payload
                                      :data
                                      :approachList))))

(re-frame/reg-event-fx
 ::fetch-approach-list
 (fn [{:keys [db] :as cofx} [_ {:keys [limit]}]]
   {:dispatch [::events/graphql-query
               {:query [[:approachList {:limit limit
                                        :offset 0}
                         [:id
                          :userName
                          :introduction
                          :image
                          [:platForms [:id :name]]]]]
                :success-handler ::on-success-fetch-approach-list
                :error-handler ::events/api-error}]}))
