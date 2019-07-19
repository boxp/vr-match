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
