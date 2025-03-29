(ns vr-match.setting.components.unlink-confirmation-alert
  (:require [reagent.core :as r]
            ["@material-ui/core/Dialog" :as Dialog]
            ["@material-ui/core/DialogTitle" :as DialogTitle]
            ["@material-ui/core/DialogActions" :as DialogActions]
            ["@material-ui/core/Button" :as Button]))

(defn unlink-confirmation-alert
  [{:keys [isOpen
           thirdPartyId
           handleClose
           handleSubmit] :as props}]
  (let [title (get {"twitter.com" "Twitterとの連携を解除しますか？"
                    "password" "登録されたEmailアドレスを削除し、認証を無効化しますか？"} thirdPartyId)]
    [:> Dialog {:open isOpen
                              :onClose handleClose
                              :aria-labelledby title}
     [:> DialogTitle title]
     [:> DialogActions
      [:> Button {:onClick handleClose}
       "キャンセル"]
      [:> Button {:onClick handleSubmit
                                :color "primary"}
       "OK"]]]))
