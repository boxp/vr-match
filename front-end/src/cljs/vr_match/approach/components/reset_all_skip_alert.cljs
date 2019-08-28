(ns vr-match.approach.components.reset-all-skip-alert
  (:require [reagent.core :as r]
            ["material-ui"]))

(defn reset-all-skip-alert
  [{:keys [isOpen
           handleClose
           handleSubmit] :as props}]
  [:> js/MaterialUI.Dialog {:open isOpen
                            :onClose handleClose
                            :aria-labelledby "スキップしたお相手をもう一度表示しますか？"
                            :aria-describedby "この操作により全てのスキップが解除され、スキップしたお相手が再び表示されます。"}
   [:> js/MaterialUI.DialogTitle "スキップしたお相手をもう一度表示しますか？" ]
   [:> js/MaterialUI.DialogContent
    [:> js/MaterialUI.DialogContentText
     "この操作により全てのスキップが解除され、スキップしたお相手が再び表示されます。"]]
   [:> js/MaterialUI.DialogActions
    [:> js/MaterialUI.Button {:onClick handleClose}
     "キャンセル"]
    [:> js/MaterialUI.Button {:onClick handleSubmit
                              :color "primary"}
     "OK"]]])
