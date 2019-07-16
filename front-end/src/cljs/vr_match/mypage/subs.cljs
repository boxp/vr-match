(ns vr-match.mypage.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::loading?
 (fn [db]
   (or
    (-> db :fetch-status :mypage (= :loading))
    (-> db :fetch-status :me (= :loading)))))

(re-frame/reg-sub
 ::platform-options
 (fn [db]
   (-> db :mypage :platform-options)))
