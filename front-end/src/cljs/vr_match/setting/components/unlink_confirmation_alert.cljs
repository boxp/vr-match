(ns vr-match.setting.components.unlink-confirmation-alert
  (:require [reagent.core :as r]
            ["@material-ui/core" :as material-ui]))

(defn unlink-confirmation-alert
  [{:keys [isOpen
           thirdPartyId
           handleClose
           handleSubmit] :as props}]
  (let [title (get {"twitter.com" "Twitterとの連携を解除しますか？"
                    "password" "登録されたEmailアドレスを削除し、認証を無効化しますか？"} thirdPartyId)]
    [:> material-ui/Dialog {:open isOpen
                            :onClose handleClose
                            :aria-labelledby title}
     [:> material-ui/DialogTitle title]
     [:> material-ui/DialogActions
      [:> material-ui/Button {:onClick handleClose}
       "キャンセル"]
      [:> material-ui/Button {:onClick handleSubmit
                              :color "primary"}
       "OK"]]]))
