(ns vr-match.profile.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::loading?
 (fn [db]
   (= (-> db :fetch-status :profile) :loading)))

(re-frame/reg-sub
 ::partner
 (fn [db]
   (-> db :profile :partner)))

(re-frame/reg-sub
 ::show-matching-dialog?
 (fn [db]
   (-> db :profile :show-matching-dialog)))
