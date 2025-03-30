(ns vr-match.approach.components.reset-all-skip-alert
  (:require [reagent.core :as r]
            ["@material-ui/core/Dialog" :as Dialog]
            ["@material-ui/core/DialogTitle" :as DialogTitle]
            ["@material-ui/core/DialogContent" :as DialogContent]
            ["@material-ui/core/DialogContentText" :as DialogContentText]
            ["@material-ui/core/DialogActions" :as DialogActions]
            ["@material-ui/core/Button" :as Button]))

(defn reset-all-skip-alert
  [{:keys [isOpen
           handleClose
           handleSubmit] :as props}]
  [:> Dialog {:open isOpen
              :onClose handleClose
              :aria-labelledby "スキップしたお相手をもう一度表示しますか？"
              :aria-describedby "この操作により全てのスキップが解除され、スキップしたお相手が再び表示されます。"}
   [:> DialogTitle "スキップしたお相手をもう一度表示しますか？" ]
   [:> DialogContent
    [:> DialogContentText
     "この操作により全てのスキップが解除され、スキップしたお相手が再び表示されます。"]]
   [:> DialogActions
    [:> Button {:onClick handleClose}
     "キャンセル"]
    [:> Button {:onClick handleSubmit
                :color "primary"}
     "OK"]]])
