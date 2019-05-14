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
