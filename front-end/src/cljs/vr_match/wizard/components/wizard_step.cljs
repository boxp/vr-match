(ns vr-match.wizard.components.wizard-step
  (:require [vr-match.lib.components.material-ui :as mui]
            [vr-match.wizard.components.wizard-title :refer [wizard-title]]))

(defn wizard-step
  [{:keys [title
           form
           me
           isNextDisabled
           handleClickNext
           handleClickSkip] :as props}]
  [mui/fade {:in true}
   [:div {:style {:padding 16}}
    [mui/grid {:container true
               :spacing 32
               :direction "column"
               :justify "space-between"
               :align-items "flex-start"
               :style {:height "100%"
                       :width "100vw"
                       :padding 32}}
     [wizard-title {:title title}]
     form
     [mui/grid {:container true
                :direction "column"}
      [mui/button {:variant "contained"
                   :color "primary"
                   :on-click handleClickNext
                   :disabled isNextDisabled}
       "次へ"]]]]])
