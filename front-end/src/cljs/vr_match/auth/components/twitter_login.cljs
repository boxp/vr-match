(ns vr-match.auth.components.twitter-login
  (:require [reagent.core :as r]
            ["material-ui"]
            [vr-match.lib.components.linear-progress :refer [linear-progress]]))

(defn twitter-login
  [{:keys [isLoading
           handleClickLoginTwitter]}]
  [:div {:style {:padding 16}}
   (when isLoading [linear-progress])
   [:> js/MaterialUI.Grid {:container true
                           :spacing 32
                           :direction "column"
                           :justify "flex-start"
                           :align-items "flex-start"
                           :style {:height "100%"
                                   :width "100vw"
                                   :padding 32}}
    [:> js/MaterialUI.Typography {:variant "display"
                                  :component "h1"
                                  :gutterBottom true
                                  :style {:text-align "left"}}
     "Twitterの認証ページへ移動しています..."]
    [:> js/MaterialUI.Button {:disabled isLoading
                              :variant "contained"
                              :color "primary"
                              :on-click handleClickLoginTwitter
                              :full-width true}
     "認証ページへ手動で移動"]]])
