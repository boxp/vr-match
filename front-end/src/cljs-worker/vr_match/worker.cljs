(ns vr-match.worker
  (:require
   [cljs.reader :refer [read-string]]
   [ajax.core :refer [ajax-request json-request-format json-response-format]]))

(defn- response
  [payload]
  (some-> payload
          pr-str
          js/postMessage))

(defn- event->payload
  [event]
  (some-> event
          .-data
          read-string))

(defmulti handle-response #(first %2))

(defmethod handle-response true
  [payload res]
  (response {:handler (-> payload :success-handler)
             :response res}))

(defmethod handle-response false
  [payload res]
  (response {:handler (-> payload :error-handler)
             :response res}))

(defn on-message
  [e]
  (let [payload (event->payload e)
        handler #(handle-response payload %)]
    (some-> payload
            (assoc :format (json-request-format))
            (assoc :response-format (json-response-format {:keywords? true}))
            (assoc :handler handler)
            ajax-request)))

(set! (.-onmessage js/self) on-message)
