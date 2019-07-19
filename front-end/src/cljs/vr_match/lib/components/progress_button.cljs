(ns vr-match.lib.components.progress-button
  (:require ["material-ui"]))

(defn progress-button
  [{:keys [loading?] :as props} children]
  [:div {:style {:position "relative"}}
   [:> js/MaterialUI.Button (-> props
                                (dissoc :loading?)
                                (assoc :disabled loading?))
    children]
   (when loading?
     [:> js/MaterialUI.CircularProgress {:size 24
                                         :style {:position "absolute"
                                                 :top "50%"
                                                 :left "50%"
                                                 :margin-top "-12"
                                                 :margin-left "-12"}}])])
