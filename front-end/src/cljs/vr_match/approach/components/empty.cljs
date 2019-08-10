(ns vr-match.approach.components.empty
  (:require
    [cljs.spec.alpha :as s]
    [reagent.core :as r]
    ["material-ui"]))

(defn swipe-cards-empty
  []
  [:div {:style {:padding 16}}
   [:> js/MaterialUI.Grid {:container true
                           :spacing 32
                           :direction "column"
                           :justify "space-between"
                           :align-items "flex-start"
                           :style {:height "100%"
                                   :width "100vw"
                                   :padding 32}}
    [:> js/MaterialUI.Typography {:variant "display1"
                                  :component "h1"
                                  :gutterBottom true
                                  :style {:text-align "left"}}
     "すべてのアバターをスワイプしました"]
    [:> js/MaterialUI.Grid {:container true
                            :spacing 32
                            :direction "column"
                            :justify "space-between"
                            :align-items "center"
                            :style {:padding 32}}
     [:> js/MaterialUI.Icon
      {:font-size "inherit"
       :color "primary"
       :style {:font-size "10em"}}
     "done"]]
    [:> js/MaterialUI.Button {:variant "contained"
                              :color "primary"
                              :style {:margin-left "auto"
                                      :margin-right "auto"}
                              :href "https://twitter.com/intent/tweet?text=Hito%20Hub%E3%81%A7VRChat%E3%81%AE%E3%83%95%E3%83%AC%E3%83%B3%E3%83%89%E3%82%84%E8%87%AA%E5%88%86%E3%81%AE%E3%81%8A%E6%B0%97%E3%81%AB%E5%85%A5%E3%82%8A%E3%81%AE%E3%82%A2%E3%83%90%E3%82%BF%E3%83%BC%E3%82%92%E3%81%95%E3%81%8C%E3%81%97%E3%81%A6%E3%81%BF%E3%82%88%E3%81%86%F0%9F%91%A6%F0%9F%91%A7&url=https://hitohub.boxp.tk&hashtags=hitohub&via=b0xp2"}
     "Hito Hubを他の人へシェアする"]]])
