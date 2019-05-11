(ns vr-match.effects
  (:require
   [re-frame.core :as re-frame]
   [re-frame.db :as db]
   [pushy.core :as pushy]))

(goog-define worker-resource-path "/static/worker/js/compiled/worker.js")

(re-frame/reg-fx
 ::set-localstorage
 (fn [[key item]]
   (.setItem js/localStorage key (pr-str item))))

(re-frame/reg-fx
 ::route
 (fn [[path]]
   (pushy/set-token! (:history @db/app-db) path)))

(re-frame/reg-fx
 ::initialize-worker
 (fn []
   (when js/Worker
     (->> (js/Worker. worker-resource-path)
          (set! (.-worker js/window))))))
