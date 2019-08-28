(ns vr-match.approach.components.empty
  (:require
    [cljs.spec.alpha :as s]
    [reagent.core :as r]
    ["material-ui"]
    [vr-match.approach.components.reset-all-skip-alert :refer [reset-all-skip-alert]]))

(defn swipe-cards-empty
  [{:keys [handleResetAllSkip] :as props}]
  (let [is-open-alert (r/atom false)
        handle-close-alert #(reset! is-open-alert false)
        handle-submit-alert (fn []
                              (reset! is-open-alert false)
                              (handleResetAllSkip))
        handle-open-alert #(reset! is-open-alert true)]
    (fn []
      [:div {:style {:padding 16}}
       [reset-all-skip-alert {:isOpen @is-open-alert
                              :handleClose handle-close-alert
                              :handleSubmit handle-submit-alert}]
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
                                  :on-click handle-open-alert}
         "スキップしたお相手をもう一度みる"]]])))
