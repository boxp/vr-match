(ns vr-match.wizard.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::platform-options
 (fn [db]
   (-> db :wizard :platform-options)))

