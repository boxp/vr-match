(ns vr-match.approach.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::approach-list
 (fn [db]
   (->> db
        :approach
        :list
        :edges
        (map :node))))

(re-frame/reg-sub
 ::show-matching-dialog?
 (fn [db]
   (-> db
       :approach
       :show-matching-dialog)))

(re-frame/reg-sub
 ::in-favorite-user
 (fn [db]
   (-> db
       :approach
       :in-favorite-user)))

(re-frame/reg-sub
  ::loaded?
  (fn [db]
    (= :loaded
       (-> db
           :fetch-status
           :approach))))
