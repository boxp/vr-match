(ns vr-match.approach.components.reset-all-skip-alert
  (:require [reagent.core :as r]
            ["@material-ui/core" :as material-ui]))

(defn reset-all-skip-alert
  [{:keys [isOpen
           handleClose
           handleSubmit] :as props}]
  [:> material-ui/Dialog {:open isOpen
                          :onClose handleClose
                          :aria-labelledby "スキップしたお相手をもう一度表示しますか？"
                          :aria-describedby "この操作により全てのスキップが解除され、スキップしたお相手が再び表示されます。"}
   [:> material-ui/DialogTitle "スキップしたお相手をもう一度表示しますか？" ]
   [:> material-ui/DialogContent
    [:> material-ui/DialogContentText
     "この操作により全てのスキップが解除され、スキップしたお相手が再び表示されます。"]]
   [:> material-ui/DialogActions
    [:> material-ui/Button {:onClick handleClose}
     "キャンセル"]
    [:> material-ui/Button {:onClick handleSubmit
                            :color "primary"}
     "OK"]]])
