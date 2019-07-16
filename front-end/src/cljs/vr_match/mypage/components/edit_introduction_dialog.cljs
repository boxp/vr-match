(ns vr-match.mypage.components.edit-introduction-dialog
  (:require
   [cljs.spec.alpha :as s]
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]))

(s/def ::isOpen boolean?)
(s/def ::introduction string?)
(s/def ::handleSubmit fn?)
(s/def ::handleCancel fn?)
(s/fdef edit-introduction-dialog
  :args (s/cat :props (s/keys :req-un [::isOpen
                                       ::introduction
                                       ::handleSubmit
                                       ::handleCancel]))
  :ret vector?)
(defn edit-introduction-dialog
  [props]
  (let [draft-introduction (r/atom (-> props :introduction))
        handle-change (fn [e]
                        (reset! draft-introduction
                                (.. e -target -value)))]
    (r/create-class
     {:display-name "edit-introduction-dialog"
      :component-did-update
      (fn [this [_ old-props]]
        (let [{:keys [introduction]} (r/props this)
              old-introduction (:introduction old-props)]
          (when (and (nil? old-introduction)
                     (seq introduction))
            (reset! draft-introduction introduction))))
      :reagent-render
      (fn [{:keys [isOpen
                   introduction
                   handleSubmit
                   handleCancel]}]
        [mui/dialog {:open isOpen
                     :onClose handleCancel
                     :aria-labelledby "自己紹介を編集"
                     :full-screen true}
         [mui/dialog-title "自己紹介を編集"]
         [mui/dialog-content
          [mui/text-field {:on-change handle-change
                           :autoFocus true
                           :full-width true
                           :multiline true
                           :margin "dense"
                           :type "text"
                           :default-value @draft-introduction}]]
         [mui/dialog-actions
          [mui/button {:on-click handleCancel}
           "キャンセル"]
          [mui/button {:on-click #(handleSubmit @draft-introduction)
                       :color "primary"}
           "決定"]]])})))
