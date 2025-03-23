(ns vr-match.lib.components.progress-button
  (:require
   ["@material-ui/core/Button" :as Button]
   ["@material-ui/core/CircularProgress" :as CircularProgress]))

(defn progress-button
  [{:keys [loading?] :as props} children]
  [:div {:style {:position "relative"}}
   [:> Button (-> props
                  (dissoc :loading?)
                  (assoc :disabled loading?))
    children]
   (when loading?
     [:> CircularProgress {:size 24
                           :style {:position "absolute"
                                   :top "50%"
                                   :left "50%"
                                   :margin-top "-12"
                                   :margin-left "-12"}}])])
