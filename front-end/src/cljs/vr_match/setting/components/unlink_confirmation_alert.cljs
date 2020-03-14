(ns vr-match.setting.components.unlink-confirmation-alert
  (:require [reagent.core :as r]
            ["material-ui"]))

(defn unlink-confirmation-alert
  [{:keys [isOpen
           thirdPartyId
           handleClose
           handleSubmit] :as props}]
  (let [title (get {"twitter.com" "Twitterとの連携を解除しますか？"
                    "password" "登録されたEmailアドレスを削除し、認証を無効化しますか？"} thirdPartyId)]
    [:> js/MaterialUI.Dialog {:open isOpen
                              :onClose handleClose
                              :aria-labelledby title}
     [:> js/MaterialUI.DialogTitle title]
     [:> js/MaterialUI.DialogActions
      [:> js/MaterialUI.Button {:onClick handleClose}
       "キャンセル"]
      [:> js/MaterialUI.Button {:onClick handleSubmit
                                :color "primary"}
       "OK"]]]))
