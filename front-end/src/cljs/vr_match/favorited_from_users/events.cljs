(ns vr-match.favorited-from-users.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.events :as events]))

(re-frame/reg-event-fx
 ::initialize
 (fn [_ _]
   {:dispatch-n [[::fetch-favorited-from-users]
                 [::events/fetch-me {:with-images? true
                                     :with-platforms? true}]]}))

(def favorited-from-users-count 12)

(re-frame/reg-event-fx
 ::on-success-fetch-favorited-from-users
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:fetch-status :favorited-from-users] :loaded)
            (assoc-in [:favorite :favorited-from-users-list] (-> data :favoritedFromUsers)))}))

(re-frame/reg-event-fx
 ::on-error-fetch-favorited-from-users
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::fetch-favorited-from-users
 (fn [{:keys [db]} _]
   (when (not= (-> db :fetch-status :favorited-from-users) :loading)
     {:db (assoc-in db [:fetch-status :favorited-from-users] :loading)
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/queries [[:favoritedFromUsers {:first favorited-from-users-count}
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
                  :success-handler ::on-success-fetch-favorited-from-users
                  :error-handler ::on-error-fetch-favorited-from-users}]})))

(re-frame/reg-event-fx
 ::on-success-fetch-next-favorited-from-users
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:fetch-status :favorited-from-users] :loaded)
            (assoc-in [:favorite :favorited-from-users-list :edges]
                      (concat
                       (-> db :favorite :favorited-from-users-list :edges)
                       (-> data :favoritedFromUsers :edges)))
            (assoc-in [:favorite :favorited-from-users-list :pageInfo]
                      (-> data :favoritedFromUsers :pageInfo)))}))

(re-frame/reg-event-fx
 ::fetch-next-favorited-from-users
 (fn [{:keys [db]} _]
   (when (not= (-> db :fetch-status :favorited-from-users) :loading)
     (let [end-cursor (-> db :favorite :favorited-from-users-list :pageInfo :endCursor)]
       {:db (assoc-in db [:fetch-status :favorited-from-users] :loading)
        :dispatch [::events/graphql-query
                   {:query
                    {:venia/queries [[:favoritedFromUsers {:first favorited-from-users-count
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
                    :success-handler ::on-success-fetch-next-favorited-from-users
                    :error-handler ::on-error-fetch-favorited-from-users}]}))))
