(ns vr-match.favorited-from-users.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::favorited-from-users
 (fn [db]
   (->> db
        :favorite
        :favorited-from-users-list
        :edges
        (map :node))))

(re-frame/reg-sub
 ::has-next-favorited-users?
 (fn [db]
   (-> db
       :favorite
       :favorited-from-users-list
       :pageInfo
       :hasNextPage)))

(re-frame/reg-sub
 ::is-loading?
 (fn [db]
   (= :loading
      (-> db
          :fetch-status
          :favorited-from-users))))

(re-frame/reg-sub
 ::is-fetched?
 (fn [db]
   (not= :none
         (-> db
             :fetch-status
             :favorited-from-users))))
