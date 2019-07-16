(ns vr-match.events
  (:require
   [re-frame.core :as re-frame]
   [venia.core :as v]
   [vr-match.effects :as effects]
   [vr-match.coeffects :as coeffects]
   [vr-match.db :as db]))

(re-frame/reg-event-fx
 ::initialize
 (fn [{:keys []} [_ {:keys [history
                            preload
                            api-endpoint]}]]
   (as-> {:db db/default-db} $
     (if preload (update $ :db #(merge % preload)))
     (if history (assoc-in $ [:db :history] history) $)
     (if api-endpoint (assoc-in $ [:db :api-endpoint] api-endpoint) $))))

(re-frame/reg-event-fx
 ::initialize-worker
 (fn [_]
   {::effects/initialize-worker {}}))

(re-frame/reg-event-db
 ::universal-push
 (fn [db [_ key params]]
   (-> db
       (assoc-in [:router :key] key)
       (assoc-in [:router :params] params))))

(re-frame/reg-event-fx
 ::push
 (fn [_ [_ path]]
   {::effects/route [path]}))

(re-frame/reg-event-fx
 ::api-error
 (fn [{:keys [db]} [_ error]]
   {:db (assoc db :api-error error)}))

(re-frame/reg-event-db
 ::open-drawer
 (fn [db _]
   (assoc-in db [:drawer :open?] true)))

(re-frame/reg-event-db
 ::close-drawer
 (fn [db _]
   (assoc-in db [:drawer :open?] false)))

(re-frame/reg-event-fx
 ::graphql-query
 [(re-frame/inject-cofx ::coeffects/local-store "session")]
 (fn [{:keys [db local-store]}
      [_ {:keys [query success-handler error-handler]}]]
   {::effects/ajax-worker [{:uri (str (-> db :api-endpoint) "/graphql")
                            :headers (if (seq local-store)
                                       {"Session" local-store}
                                       {})
                            :method :post
                            :params {:query (v/graphql-query query)
                                     :variables {}}
                            :success-handler success-handler
                            :error-handler error-handler}]}))

(re-frame/reg-event-fx
 ::set-session
 (fn [_ [_ session]]
   {::effects/set-localstorage {:key "session"
                                :item session}}))

(re-frame/reg-event-fx
 ::on-success-fetch-me
 (fn [{:keys [db]}
      [_ {:keys [data]}]]
   {:db
    (-> db
        (assoc-in [:fetch-status :me] :loaded)
        (update :me #(merge % (-> data :me))))}))

(re-frame/reg-event-fx
 ::on-error-fetch-me
 (fn [{:keys [db]}
      [_ {:keys [errors]}]]
   {:db (assoc-in db [:fetch-status :me] :loaded)
    :dispatch (case (-> errors first :extensions :type)
                "invalid-session" [::push "/"]
                [::api-error errors])}))

(re-frame/reg-event-fx
 ::fetch-me
 (fn [{:keys [db]}
      [_ {:keys [with-images?
                 with-platforms?]}]]
   (when (-> db :fetch-status :me (not= :loading))
     (let [me-props (vec (cond->> [:id :name :introduction]
                           (and with-images? with-platforms?) (concat [[:images [:id :url]]
                                                                       [:platforms [:id :name :url :platformUserId]]])
                           with-images? (concat [[:images [:id :url]]])
                           with-platforms? (concat [[:platforms [:id :name :url :platformUserId]]])
                           :always identity))]
       {:db (assoc-in db [:fetch-status :me] :loading)
        :dispatch [::graphql-query
                   {:query
                    {:venia/queries [[:me
                                      me-props]]}
                    :success-handler ::on-success-fetch-me
                    :error-handler ::on-error-fetch-me}]}))))
