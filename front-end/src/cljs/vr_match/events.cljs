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
 (fn [{:keys [db]} [_ {:keys [query success-handler error-handler]}]]
   {::effects/ajax-worker [{:uri (str (-> db :api-endpoint) "/graphql")
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
