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
    [:> js/MaterialUI.Typography {:variant "body1"
                                  :component "p"}
     "新たなアバターの登録をお待ちください"]]])
