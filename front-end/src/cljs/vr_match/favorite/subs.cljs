(ns vr-match.favorite.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::favorited-users-from-me
 (fn [db]
   (->> db
        :favorite
        :favorited-from-me-list
        :edges
        (map :node))))

(re-frame/reg-sub
 ::has-next-favorited-users?
 (fn [db]
   (-> db
       :favorite
       :favorited-from-me-list
       :pageInfo
       :hasNextPage)))

(re-frame/reg-sub
 ::is-loading?
 (fn [db]
   (= :loading
      (-> db
          :fetch-status
          :favorite))))
