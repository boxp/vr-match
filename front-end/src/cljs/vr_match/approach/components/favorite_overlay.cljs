(ns vr-match.approach.components.favorite-overlay
  (:require [vr-match.lib.components.material-ui :as mui]))

(defn favorite-overlay []
  [:div {:style {:width "100%"
                 :height "100%"
                 :border-radius "4px"
                 :background-color mui/favorite-color}}
   [:div {:style {:padding "8px"
                  :color "white"
                  :font-size "48px"}}
    [mui/icon {:fontSize "inherit"
               :style {:font-size "48px"}}
     "favorite"]
    [:div {:style {:margin-top "8px"}}
    [mui/typo-graphy {:fontSize "inherit"
                      :color "inherit"}
     "お気に入り"]]]])

