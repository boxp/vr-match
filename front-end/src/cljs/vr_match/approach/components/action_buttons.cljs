(ns vr-match.approach.components.action-buttons
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [vr-match.lib.components.material-ui :as mui]))

(def action-buttons-styles
  #js {"root" #js {}})

(defn action-buttons-component
  [{:keys [classes
           onClickSkip
           onClickFavorite] :as props}]
  [mui/grid {:container true
             :justify-content "space-around"}
   [mui/fab {:aria-label "スキップ"
                :on-click onClickSkip}
    [mui/icon "reply"]]
   [mui/fab {:color "secondary"
                :aria-label "すき"
                :on-click onClickFavorite}
    [mui/icon "favorite"]]])

(def action-buttons
  (r/adapt-react-class ((mui/with-styles action-buttons-styles) (r/reactify-component action-buttons-component))))
