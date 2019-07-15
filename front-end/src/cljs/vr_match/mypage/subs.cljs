(ns vr-match.mypage.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::loading?
 (fn [db]
   (-> db :fetch-status :mypage (= :loading))))

