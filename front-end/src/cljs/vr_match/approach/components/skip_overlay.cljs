(ns vr-match.approach.components.skip-overlay
  (:require [vr-match.lib.components.material-ui :as mui]))

(defn skip-overlay []
  [:div {:style {:width "100%"
                 :height "100%"
                 :border-radius "4px"
                 :background-color mui/skip-color
                 :display "flex"
                 :justify-content "flex-end"}}
   [:div {:style {:padding "8px"
                  :color "white"
                  :font-size "48px"}}
    [mui/icon {:fontSize "inherit"
               :style {:font-size "48px"}}
     "reply"]
    [:div {:style {:margin-top "8px"}}
     [mui/typo-graphy {:fontSize "inherit"
                       :color "inherit"}
      "スキップ"]]]])

