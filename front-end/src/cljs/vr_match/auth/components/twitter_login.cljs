(ns vr-match.auth.components.twitter-login
  (:require [reagent.core :as r]
            ["@material-ui/core/Grid" :as Grid]
            ["@material-ui/core/Typography" :as Typography]
            ["@material-ui/core/Button" :as Button]
            [vr-match.lib.components.linear-progress :refer [linear-progress]]))

(defn twitter-login
  [{:keys [isLoading
           handleClickLoginTwitter]}]
  [:<>
   (when isLoading [linear-progress])
   [:div {:style {:padding 16}}
    [:> Grid {:container true
              :spacing 32
              :direction "column"
              :justify "space-between"
              :align-items "flex-start"
              :style {:height "100%"
                      :width "100vw"
                      :padding 32}}
     [:> Typography {:variant "display1"
                     :component "h1"
                     :gutterBottom true
                     :style {:text-align "left"}}
      "Twitter認証の準備中..."]
     [:> Button {:disabled isLoading
                 :variant "contained"
                 :color "primary"
                 :on-click handleClickLoginTwitter
                 :full-width true}
      "認証ページへ手動で移動"]]]])
