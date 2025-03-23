(ns vr-match.lib.components.linear-progress
  (:require
    [reagent.core :as r]
    ["@material-ui/core/LinearProgress" :as LinearProgress]
    [vr-match.lib.components.elevation :as elevation]))

(defn linear-progress []
  [:div {:style {:width "100%"
                 :position "fixed"
                 :top 0
                 :z-index elevation/linear-progress}}
   [:> LinearProgress]])
