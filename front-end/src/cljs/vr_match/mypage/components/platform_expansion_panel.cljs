(ns vr-match.mypage.components.platform-expansion-panel
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [vr-match.lib.models.platform :as platform-model]
            ["material-ui"]))

(s/def ::name string?)
(s/def ::id number?)
(s/def ::exampleUserId string?)
(s/def ::platformOption
  (s/keys :req-un [::name
                   ::id
                   ::exampleUserId]))
(s/def ::platformOptions (s/coll-of ::platformOption))
(s/def ::platform ::platform-model/platform)
(s/def ::platforms (s/coll-of ::platform-model/platform))

(s/fdef platform->placeholder
  :args (s/cat :platform ::platform
               :platformOptions ::platformOptions)
  :ret string?)
(defn- platform->placeholder
  [platform platformOptions]
  (->> platformOptions
       (filter #(= (:id platform) (:id %)))
       first
       :exampleUserId))

(s/def ::platformIdx number?)
(s/def ::handleChangePlatform fn?)
(s/def ::handleClickDelete fn?)
(s/def ::handleChangePlatformUserId fn?)
(s/fdef platform-expansion-panel
  :args (s/cat :props
               (s/keys :req-un [::platform
                                ::platformIdx
                                ::platformOptions
                                ::handleChangePlatform
                                ::handleClickDelete
                                ::handleChangePlatformUserId])))
(defn platform-expansion-panel
  [props]
  (fn [{:keys [platform
               platformIdx
               platformOptions
               handleChangePlatform
               handleClickDelete
               handleChangePlatformUserId]}]
    [:> js/MaterialUI.ExpansionPanel
     [:> js/MaterialUI.ExpansionPanelSummary
      {:expand-icon (r/as-element
                     [:> js/MaterialUI.Icon "expand_more"])}
      [:> js/MaterialUI.Typography
       (-> platform :name)]]
     [:> js/MaterialUI.ExpansionPanelDetails
      [:form
       [:> js/MaterialUI.FormControl
        [:> js/MaterialUI.InputLabel
         {:html-for (str "platform-selector-" platformIdx)}
         "活動場所"]
        [:> js/MaterialUI.Select
         {:value (-> platform :id)
          :onChange handleChangePlatform
          :full-width true
          :input-props #js {"name" "platform-selector"
                            "id" (str "platform-selector-" platformIdx)}}
         (map (fn [{:keys [id
                           name] :as option}]
                ^{:key id}
                [:> js/MaterialUI.MenuItem
                 {:value id}
                 name])
              platformOptions)]
        [:> js/MaterialUI.TextField
         {:label "ID"
          :margin "dense"
          :type "text"
          :full-width true
          :on-change handleChangePlatformUserId
          :default-value (:userId platform)
          :placeholder (platform->placeholder
                        platform
                        platformOptions)}]]]]
     [:> js/MaterialUI.ExpansionPanelActions
      [:> js/MaterialUI.IconButton
       {:on-click handleClickDelete}
       [:> js/MaterialUI.Icon
        "delete"]]]]))
