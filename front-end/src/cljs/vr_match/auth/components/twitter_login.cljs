(ns vr-match.auth.components.twitter-login
  (:require [reagent.core :as r]
            ["@material-ui/core" :as material-ui]
            [vr-match.lib.components.linear-progress :refer [linear-progress]]))

(defn twitter-login
  [{:keys [isLoading
           handleClickLoginTwitter]}]
  [:<>
   (when isLoading [linear-progress])
   [:div {:style {:padding 16}}
    [:> material-ui/Grid {:container true
                          :spacing 32
                          :direction "column"
                          :justify "space-between"
                          :align-items "flex-start"
                          :style {:height "100%"
                                  :width "100vw"
                                  :padding 32}}
     [:> material-ui/Typography {:variant "display1"
                                 :component "h1"
                                 :gutterBottom true
                                 :style {:text-align "left"}}
      "Twitter認証の準備中..."]
     [:> material-ui/Button {:disabled isLoading
                             :variant "contained"
                             :color "primary"
                             :on-click handleClickLoginTwitter
                             :full-width true}
      "認証ページへ手動で移動"]]]])
