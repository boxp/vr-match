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

(defn- remove-edge-by-id
  [db id]
  (update-in db [:approach :list :edges]
             (fn [edges]
               (remove #(-> % :node :id (= id)) edges))))

(re-frame/reg-event-fx
 ::skip
 (fn [{:keys [db] :as cofx} [_ {:keys [id]}]]
   {:db (-> db
            (update-in [:approach :list :edges] rest))}))

(re-frame/reg-event-fx
 ::favorite
 (fn [{:keys [db] :as cofx} [_ {:keys [id]}]]
   {:db (-> db
            (update-in [:approach :list :edges] rest))}))

(re-frame/reg-event-fx
 ::fetch-approach-list
 (fn [{:keys [db] :as cofx} [_ {:keys [count]}]]
   {:dispatch [::events/graphql-query
               {:query {:venia/queries [[:approachList {:first count}
                         [[:edges
                           [:cursor
                            [:node
                             [:id
                              :name
                              :introduction
                              [:images [:id :url]]
                              [:platforms [:id :name]]]]]]]]]}
                :success-handler ::on-success-fetch-approach-list
                :error-handler ::events/api-error}]}))

(re-frame/reg-event-fx
 ::fetch-next-approach-list
 (fn [{:keys [db] :as cofx} [_ {:keys [count]}]]
   (let [after (some-> db :approach :list :edges last :cursor)]
     (if after
       {:dispatch [::events/graphql-query
                   {:query [[:approachList {:first count
                                            :after after}
                             [[:edges
                               [:cursor
                                [:node
                                 [:id
                                  :name
                                  :introduction
                                  [:images [:id :url]]
                                  [:platForms [:id :name]]]]]]]]]
                    :success-handler ::on-success-fetch-approach-list
                    :error-handler ::events/api-error}]}
       {}))))
