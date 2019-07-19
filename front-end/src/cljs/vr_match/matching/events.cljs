(ns vr-match.matching.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.events :as events]))

(def per-page 12)

(re-frame/reg-event-fx
 ::initialize
 (fn [_ _]
   {:dispatch-n [[::fetch-matching]
                 [::events/fetch-me {:with-platforms? true
                                     :with-images? true}]]}))

(re-frame/reg-event-fx
 ::on-success-fetch-matching
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:fetch-status :matching] :loaded)
            (assoc-in [:matching :list] (-> data :matchedUsers)))}))

(re-frame/reg-event-fx
 ::on-error-fetch-matching
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::fetch-matching
 (fn [{:keys [db]} _]
   (when (not= (-> db :fetch-status :matching) :loading)
     {:db (assoc-in db [:fetch-status :matching] :loading)
      :dispatch [::events/graphql-query
                 {:query
                  {:venia/queries [[:matchedUsers {:first per-page}
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
                  :success-handler ::on-success-fetch-matching
                  :error-handler ::on-error-fetch-matching}]})))

(re-frame/reg-event-fx
 ::on-success-fetch-next-matching
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:fetch-status :matching] :loaded)
            (assoc-in [:matching :list :edges]
                      (concat
                       (-> db :matching :list :edges)
                       (-> data :matchedUsers :edges)))
            (assoc-in [:matching :list :pageInfo]
                      (-> data :matchedUsers :pageInfo)))}))

(re-frame/reg-event-fx
 ::fetch-next-matching
 (fn [{:keys [db]} _]
   (when (not= (-> db :fetch-status :matching) :loading)
     (let [end-cursor (-> db :matching :list :pageInfo :endCursor)]
       {:db (assoc-in db [:fetch-status :matching] :loading)
        :dispatch [::events/graphql-query
                   {:query
                    {:venia/queries [[:matchedUsers {:first per-page
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
                    :success-handler ::on-success-fetch-next-matching
                    :error-handler ::on-error-fetch-matching}]}))))

