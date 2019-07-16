(ns vr-match.lib.components.linear-progress
  (:require
    [reagent.core :as r]
    ["material-ui"]
    [vr-match.lib.components.elevation :as elevation]))

(defn linear-progress []
  [:div {:style {:width "100%"
                 :position "fixed"
                 :top 0
                 :z-index elevation/linear-progress}}
            [:> js/MaterialUI.LinearProgress]])
