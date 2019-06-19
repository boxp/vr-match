(ns vr-match.auth.components.linear-progress
  (:require
    [reagent.core :as r]
    ["material-ui"]))

(defn linear-progress []
  [:div {:style {:width "100%"
                 :position "fixed"
                 :top 0}}
            [:> js/MaterialUI.LinearProgress]])
