(ns vr-match.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::router
 (fn [db]
   (:router db)))

(re-frame/reg-sub
 ::theme
 (fn [db]
   (:theme db)))

(re-frame/reg-sub
 ::open-drawer?
 (fn [db]
   (-> db :drawer :open?)))

(re-frame/reg-sub
 ::me
 (fn [db]
   (-> db :me)))

(re-frame/reg-sub
 ::loading-me?
 (fn [db]
   (= (-> db :fetch-status :me) :loading)))
