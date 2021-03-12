(ns vr-match.lib.components.progress-button
  (:require ["@material-ui/core" :as material-ui]))

(defn progress-button
  [{:keys [loading?] :as props} children]
  [:div {:style {:position "relative"}}
   [:> material-ui/Button (-> props
                              (dissoc :loading?)
                              (assoc :disabled loading?))
    children]
   (when loading?
     [:> material-ui/CircularProgress {:size 24
                                       :style {:position "absolute"
                                               :top "50%"
                                               :left "50%"
                                               :margin-top "-12"
                                               :margin-left "-12"}}])])
