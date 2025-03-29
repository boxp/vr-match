(ns vr-match.setting.components.cannot-unlink-third-party-alert
  (:require [reagent.core :as r]
            ["@material-ui/core/Dialog" :as Dialog]
            ["@material-ui/core/DialogTitle" :as DialogTitle]
            ["@material-ui/core/DialogContent" :as DialogContent]
            ["@material-ui/core/DialogContentText" :as DialogContentText]
            ["@material-ui/core/DialogActions" :as DialogActions]
            ["@material-ui/core/Button" :as Button]))

(defn cannot-unlink-third-party-alert
  [{:keys [isOpen
           handleClose] :as props}]
  (let [title "全ての認証先を解除することはできません"
        content "この認証を無効にしたい場合、代わりに他の認証を有効にしてください"]
    [:> Dialog {:open isOpen
                              :onClose handleClose
                              :aria-labelledby title
                              :aria-describedby content}
     [:> DialogTitle title]
     [:> DialogContent
      [:> DialogContentText content]]
     [:> DialogActions
      [:> Button {:onClick handleClose}
       "閉じる"]]]))
