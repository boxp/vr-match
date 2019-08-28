(ns vr-match.approach.events
  (:require
   [re-frame.core :as re-frame]
   [vr-match.events :as events]))

(def user-per-page 12)

(re-frame/reg-event-fx
 ::initialize
 (fn [_ _]
   {:dispatch-n [[::fetch-approach-list]
                 [::events/fetch-me {:with-images? true}]]}))

(defn- remove-edge-by-id
  [db id]
  (update-in db [:approach :list :edges]
             (fn [edges]
               (remove #(-> % :node :id (= id)) edges))))

(re-frame/reg-event-fx
 ::on-success-skip
 (fn [{:keys [db]} _]
   {}))

(re-frame/reg-event-fx
 ::on-error-skip
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::skip
 (fn [{:keys [db] :as cofx} [_ id]]
   {:db (-> db
            (update-in [:approach :list :edges] rest))
    :dispatch [::events/graphql-query
               {:query
                {:venia/operation {:operation/type :mutation
                                   :operation/name "skip"}
                 :venia/queries [[:skip {:partnerId id}]]}
                :success-handler ::on-success-skip
                :error-handler ::on-error-skip}]}))

(re-frame/reg-event-fx
 ::on-success-favorite
 (fn [{:keys [db]} [_ {:keys [data]}]]
   {:db (-> db
            (assoc-in [:approach :show-matching-dialog] (-> data :favorite :isMatched)))}))

(re-frame/reg-event-fx
 ::on-error-favorite
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])}))

(re-frame/reg-event-fx
 ::favorite
 (fn [{:keys [db] :as cofx} [_ {:keys [id] :as user}]]
   {:db (-> db
            (update-in [:approach :list :edges] rest)
            (assoc-in [:approach :in-favorite-user] user))
    :dispatch [::events/graphql-query
               {:query
                {:venia/operation {:operation/type :mutation
                                   :operation/name "favorite"}
                 :venia/queries [[:favorite {:partnerId id}
                                  [:isMatched]]]}
                :success-handler ::on-success-favorite
                :error-handler ::on-error-favorite}]}))

(re-frame/reg-event-fx
 ::on-success-reset-all-skip
 (fn [{:keys [db]} _]
   {:dispatch [::fetch-approach-list]
    :db (-> db
            (assoc-in [:fetch-status :approach] :loaded))}))

(re-frame/reg-event-fx
 ::on-error-reset-all-skip
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])
    :db (-> db
            (assoc-in [:fetch-status :approach] :loaded))}))

(re-frame/reg-event-fx
 ::reset-all-skip
 (fn [{:keys [db] :as cofx} [_ id]]
   {:dispatch [::events/graphql-query
               {:query
                {:venia/operation {:operation/type :mutation
                                   :operation/name "resetAllSkip"}
                 :venia/queries [[:resetAllSkip]]}
                :success-handler ::on-success-reset-all-skip
                :error-handler ::on-error-reset-all-skip}]
    :db (assoc-in db [:fetch-status :approach] :loading)}))

(re-frame/reg-event-db
  ::on-success-fetch-approach-list
  (fn [db [_ {:keys [data errors] :as payload}]]
    (-> db
        (assoc-in [:approach :list] (-> payload
                                        :data
                                        :approachList))
        (assoc-in [:fetch-status :approach] :loaded))))

(re-frame/reg-event-fx
 ::on-error-fetch-approach-list
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])
    :db (-> db
            (assoc-in [:fetch-status :approach] :loaded))}))

(re-frame/reg-event-fx
  ::fetch-approach-list
  (fn [{:keys [db] :as cofx} _]
    (when-not (= :loading (-> db :fetch-status :approach))
      {:dispatch [::events/graphql-query
                  {:query {:venia/queries
                           [[:approachList {:first user-per-page}
                             [[:edges
                               [:cursor
                                [:node
                                 [:id
                                  :name
                                  :introduction
                                  [:images [:id :url]]
                                  [:platforms [:id :name]]]]]]
                              [:pageInfo
                               [:hasNextPage]]]]]}
                   :success-handler ::on-success-fetch-approach-list
                   :error-handler ::on-error-fetch-approach-list}]
       :db (assoc-in db [:fetch-status :approach] :loading)})))

(re-frame/reg-event-db
  ::on-success-fetch-next-approach-list
  (fn [db [_ {:keys [data errors] :as payload}]]
    (-> db
        (assoc-in [:fetch-status :approach] :loaded)
        (update-in [:approach :list :edges] #(concat % (-> payload
                                                           :data
                                                           :approachList
                                                           :edges))))))

(re-frame/reg-event-fx
 ::on-error-fetch-next-approach-list
 (fn [{:keys [db]}
      [_ {:keys [errors] :as payload}]]
   {:dispatch-n (case (-> errors first :extensions :type)
                  "invalid-session" [[::events/push "/"]
                                     [::events/clear-session]]
                  [[::events/api-error errors]])
    :db (-> db
            (assoc-in [:fetch-status :approach] :loaded))}))

(re-frame/reg-event-fx
  ::fetch-next-approach-list
  (fn [{:keys [db] :as cofx} _]
    (when-not (= :loading (-> db :fetch-status :approach))
      {:dispatch [::events/graphql-query
                  {:query {:venia/queries
                           [[:approachList {:first user-per-page}
                             [[:edges
                               [:cursor
                                [:node
                                 [:id
                                  :name
                                  :introduction
                                  [:images [:id :url]]
                                  [:platforms [:id :name]]]]]]
                              [:pageInfo
                               [:hasNextPage]]]]]}
                   :success-handler ::on-success-fetch-next-approach-list
                   :error-handler ::on-error-fetch-next-approach-list}]
       :db (assoc-in db [:fetch-status :approach] :loading)})))

(re-frame/reg-event-fx
 ::close-matching-dialog
 (fn [{:keys [db]}]
   {:db (-> db
            (assoc-in [:approach :show-matching-dialog] false))}))
