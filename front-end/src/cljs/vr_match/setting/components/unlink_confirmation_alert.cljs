(ns vr-match.setting.components.unlink-confirmation-alert
  (:require [reagent.core :as r]
            ["material-ui"]))

(defn unlink-confirmation-alert
  [{:keys [isOpen
           thirdPartyName
           handleClose
           handleSubmit] :as props}]
  (let [title (str thirdPartyName "との連携を解除しますか？")]
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
