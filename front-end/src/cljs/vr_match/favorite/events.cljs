(ns vr-match.favorite.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.events :as events]))

(re-frame/reg-event-fx
 ::initialize
 (fn [_ _]
   {:dispatch [::fetch-favorited-from-me]}))

(def favorited-from-me-count 12)

(re-frame/reg-event-fx
 ::on-success-fetch-favorited-from-me
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:fetch-status :favorite] :loaded)
            (assoc-in [:favorite :favorited-from-me-list] (-> data :favoritedUsers)))}))

(re-frame/reg-event-fx
 ::on-error-fetch-favorited-from-me
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::fetch-favorited-from-me
 (fn [{:keys [db]} _]
   (when (not= (-> db :fetch-status :favorite) :loading)
     {:db (assoc-in db [:fetch-status :favorite] :loading)
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/queries [[:favoritedUsers {:first favorited-from-me-count}
                                    [[:edges
                                      [:cursor
                                       [:node
                                        [:id
                                         :name
                                         :introduction
                                         [:images [:id :url]]
                                         [:platforms [:id :name]]]]]]
                                     [:pageInfo
                                      [:startCursor
                                       :endCursor
                                       :hasPreviousPage
                                       :hasNextPage]]]]]}
                  :success-handler ::on-success-fetch-favorited-from-me
                  :error-handler ::on-error-fetch-favorited-from-me}]})))

(re-frame/reg-event-fx
 ::on-success-fetch-next-favorited-from-me
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:fetch-status :favorite] :loaded)
            (assoc-in [:favorite :favorited-from-me-list :edges]
                      (concat
                       (-> db :favorite :favorited-from-me-list :edges)
                       (-> data :favoritedUsers :edges)))
            (assoc-in [:favorite :favorited-from-me-list :pageInfo]
                      (-> data :favoritedUsers :pageInfo)))}))

(re-frame/reg-event-fx
 ::fetch-next-favorited-from-me
 (fn [{:keys [db]} _]
   (when (not= (-> db :fetch-status :favorite) :loading)
     (let [end-cursor (-> db :favorite :favorited-from-me-list :pageInfo :endCursor)]
       {:db (assoc-in db [:fetch-status :favorite] :loading)
        :dispatch [::events/graphql-query
                   {:query
                    {:venia/queries [[:favoritedUsers {:first favorited-from-me-count
                                                       :after end-cursor}
                                      [[:edges
                                        [:cursor
                                         [:node
                                          [:id
                                           :name
                                           :introduction
                                           [:images [:id :url]]
                                           [:platforms [:id :name]]]]]]
                                       [:pageInfo
                                        [:startCursor
                                         :endCursor
                                         :hasPreviousPage
                                         :hasNextPage]]]]]}
                    :success-handler ::on-success-fetch-next-favorited-from-me
                    :error-handler ::on-error-fetch-favorited-from-me}]}))))
