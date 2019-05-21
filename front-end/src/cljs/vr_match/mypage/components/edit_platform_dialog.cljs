(ns vr-match.mypage.components.edit-platform-dialog
  (:require
   [cljs.spec.alpha :as s]
   [reagent.core :as r]
   [vr-match.lib.models.plat-form :as platform-model]
   ["material-ui"]))

(s/def ::name string?)
(s/def ::id string?)
(s/def ::exampleId string?)
(s/def ::platformOption
  (s/keys :req-un [::name
                   ::id
                   ::exampleId]))

(s/def ::isOpen boolean)
(s/def ::platforms (s/coll-of ::platform-model/platForm))
(s/def ::platformOptions (s/coll-of ::platformOption))
(s/def ::handleClickSubmit fn?)
(s/def ::handleCancel fn?)
(s/fdef edit-platform-dialog
  :args (s/cat :props (s/keys :req-un [::isOpen
                                       ::platForms
                                       ::platformOptions
                                       ::handleClick
                                       ::handleCancel]))
  :ret vector?)
(defn edit-platform-dialog
  [props]
  (let [draft-platforms (r/atom (-> props :platforms))]
    (fn [{:keys [isOpen
                 platforms
                 platformOptions
                 handleSubmit
                 handleCancel]}]
      [:> js/MaterialUI.Dialog {:open isOpen
                                :onClose handleCancel
                                :aria-labelledby "活動場所を編集"
                                :full-screen true}
       [:> js/MaterialUI.DialogTitle "活動場所を編集"]
       [:> js/MaterialUI.DialogContent
        [:> js/MaterialUI.ExpansionPanel
         [:> js/MaterialUI.ExpansionPanelSummary
          {:expand-icon (r/as-element
                         [:> js/MaterialUI.Icon "expand_more"])}
          [:> js/MaterialUI.Typography "hoge"]]
         [:> js/MaterialUI.ExpansionPanelDetails
          [:form
           [:> js/MaterialUI.FormControl
            [:> js/MaterialUI.InputLabel
             {:html-for "hoge"}
             "活動場所"]
            [:> js/MaterialUI.Select
             {:value "1"
              :onChange #()
              :full-width true
              :input-props #js {"name" "platform"
                                "id" "hoge"}}
             [:> js/MaterialUI.MenuItem {:value "1"} "VRChat"]]
            [:> js/MaterialUI.TextField
             {:label "ID"
              :margin "dense"
              :type "text"
              :full-width true
              :placeholder "usr_3b6403c3-be9f-432c-ab1f-446778946421"}]]]]
         [:> js/MaterialUI.ExpansionPanelActions
          [:> js/MaterialUI.Button
           {:on-click #()}
           "削除"]]]]
       [:> js/MaterialUI.DialogActions
        [:> js/MaterialUI.Button {:on-click handleCancel}
         "キャンセル"]
        [:> js/MaterialUI.Button {:on-click handleSubmit
                                  :color "primary"}
         "決定"]]])))
