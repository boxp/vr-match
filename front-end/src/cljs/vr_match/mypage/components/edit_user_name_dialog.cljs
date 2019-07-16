(ns vr-match.mypage.components.edit-user-name-dialog
  (:require
   [cljs.spec.alpha :as s]
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]))

(s/def ::isOpen boolean?)
(s/def ::userName string?)
(s/def ::handleClickSubmit fn?)
(s/def ::handleCancel fn?)
(s/fdef edit-user-name-dialog
  :args (s/cat :props (s/keys :req-un [::isOpen
                                       ::userName
                                       ::handleClickSubmit
                                       ::handleCancel]))
  :ret vector?)
(defn edit-user-name-dialog
  [props]
  (let [draft-user-name (r/atom (-> props :userName))
        handle-change (fn [e] (some->> e .-target .-value (reset! draft-user-name)))]
    (r/create-class
     {:display-name "edit-user-name-dialog"
      :component-did-update
      (fn [this [_ old-props]]
        (let [{:keys [userName]} (r/props this)
              old-userName (:userName old-props)]
          (when (and (nil? old-userName)
                     (seq userName))
            (reset! draft-user-name userName))))
      :reagent-render
      (fn [{:keys [isOpen
                   userName
                   handleSubmit
                   handleCancel]}]
        [mui/dialog {:open isOpen
                     :onClose handleCancel
                     :aria-labelledby "ユーザー名を編集"}
         [mui/dialog-title "ユーザー名を編集"]
         [mui/dialog-content
          [mui/text-field {:autoFocus true
                           :margin "dense"
                           :label "ユーザー名"
                           :type "text"
                           :on-change handle-change
                           :default-value @draft-user-name}]]
         [mui/dialog-actions
          [mui/button {:on-click handleCancel}
           "キャンセル"]
          [mui/button {:on-click #(handleSubmit @draft-user-name)
                       :color "primary"}
           "決定"]]])})))
