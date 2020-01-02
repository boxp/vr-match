(ns vr-match.matching.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::matching-users
 (fn [db]
   (->> db
        :matching
        :list
        :edges
        (map :node))))

(re-frame/reg-sub
 ::has-next-matching-users?
 (fn [db]
   (-> db
       :matching
       :list
       :pageInfo
       :hasNextPage)))

(re-frame/reg-sub
 ::is-loading?
 (fn [db]
   (= :loading
      (-> db
          :fetch-status
          :matching))))

(re-frame/reg-sub
 ::is-fetched?
 (fn [db]
   (not= :none
         (-> db
             :fetch-status
             :matching))))
