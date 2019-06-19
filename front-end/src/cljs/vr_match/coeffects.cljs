(ns vr-match.coeffects
  (:require
   [cljs.reader :as reader]
   [re-frame.core :as re-frame]
   [re-frame.db :as re-frame-db]
   [pushy.core :as pushy]))

(re-frame/reg-cofx
 ::local-store
 (fn [coeffects key]
   (assoc coeffects
          :local-store
          (.getItem js/localStorage key))))

(re-frame/reg-cofx
 ::route
 (fn [coeffects]
   (assoc coeffects
          :route
          (some->
           @re-frame-db/app-db
           :history
           pushy/get-token))))

(re-frame/reg-cofx
 ::session
 (fn [coeffects]
   (assoc coeffects
          :session
          (.getItem js/localStorage "session"))))
