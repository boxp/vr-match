(ns vr-match.setting.components.cannot-unlink-third-party-alert
  (:require [reagent.core :as r]
            ["@material-ui/core" :as material-ui]))

(defn cannot-unlink-third-party-alert
  [{:keys [isOpen
           handleClose] :as props}]
  (let [title "全ての認証先を解除することはできません"
        content "この認証を無効にしたい場合、代わりに他の認証を有効にしてください"]
    [:> material-ui/Dialog {:open isOpen
                            :onClose handleClose
                            :aria-labelledby title
                            :aria-describedby content}
     [:> material-ui/DialogTitle title]
     [:> material-ui/DialogContent
      [:> material-ui/DialogContentText content]]
     [:> material-ui/DialogActions
      [:> material-ui/Button {:onClick handleClose}
       "閉じる"]]]))
