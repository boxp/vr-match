(ns vr-match.worker
  (:require
   [cljs.reader :refer [read-string]]
   [ajax.core :refer [ajax-request json-request-format json-response-format]]))

(defn- response
  [payload]
  (some-> payload
          pr-str
          (.postMessage js/self)))

(defn- event->payload
  [event]
  (some-> event
          .-data
          read-string))

(defmulti handle-response #(first %2))

(defmethod handle-response true
  [payload res]
  (response {:handler (-> payload :success-handler)
             :response (-> res second)}))

(defmethod handle-response false
  [payload res]
  (response {:handler (-> payload :error-handler)
             :response (-> res second :response)}))

(defn handle-message
  [e]
  (let [payload (event->payload e)
        handler #(handle-response payload %)]
    (some-> payload
            (assoc :format (json-request-format))
            (assoc :response-format (json-response-format {:keywords? true}))
            (assoc :handler handler)
            ajax-request)))

;; shadow-cljsのWeb Worker初期化関数
(defn init []
  (js/console.log "Web Worker initialized")
  (.addEventListener js/self "message" handle-message))