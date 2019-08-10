(ns vr-match.effects
  (:require
   [cljs.reader :refer [read-string]]
   [re-frame.core :as re-frame]
   [re-frame.db :as db]
   [pushy.core :as pushy]))

(goog-define worker-resource-path "/static/worker/js/compiled/worker.js")

(re-frame/reg-fx
 ::set-localstorage
 (fn [{:keys [key item]}]
   (.setItem js/localStorage key item)))

(re-frame/reg-fx
 ::remove-localstorage
 (fn [{:keys [key]}]
   (.removeItem js/localStorage key)))

(re-frame/reg-fx
 ::route
 (fn [[path]]
   (pushy/set-token! (:history @db/app-db) path)))

(defn- on-worker-message
  [e]
  (when-let [payload (some-> e .-data read-string)]
    (re-frame/dispatch [(:handler payload)
                        (-> payload :response)])))

(re-frame/reg-fx
 ::initialize-worker
 (fn []
   (when js/Worker
     (let [worker (js/Worker. worker-resource-path)]
       (set! (.-onmessage worker) on-worker-message)
       (set! (.-worker js/window) worker)))))

(re-frame/reg-fx
 ::ajax-worker
 (fn [[params]]
   (some-> js/window
           .-worker
           (.postMessage (pr-str params)))))

(re-frame/reg-fx
  ::ga-page-view
  (fn [[path]]
    (when (exists? js/ga)
      (js/ga "set" "page" path)
      (js/ga "send" "pageview"))))
