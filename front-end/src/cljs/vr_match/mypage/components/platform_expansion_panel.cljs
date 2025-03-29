(ns vr-match.mypage.components.platform-expansion-panel
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [vr-match.lib.models.platform :as platform-model]
            ["@material-ui/core/ExpansionPanel" :as ExpansionPanel]
            ["@material-ui/core/ExpansionPanelSummary" :as ExpansionPanelSummary]
            ["@material-ui/core/ExpansionPanelDetails" :as ExpansionPanelDetails]
            ["@material-ui/core/ExpansionPanelActions" :as ExpansionPanelActions]
            ["@material-ui/core/Typography" :as Typography]
            ["@material-ui/core/Icon" :as Icon]
            ["@material-ui/core/FormControl" :as FormControl]
            ["@material-ui/core/InputLabel" :as InputLabel]
            ["@material-ui/core/Select" :as Select]
            ["@material-ui/core/MenuItem" :as MenuItem]
            ["@material-ui/core/TextField" :as TextField]
            ["@material-ui/core/IconButton" :as IconButton]))

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
    [:> ExpansionPanel
     [:> ExpansionPanelSummary
      {:expand-icon (r/as-element
                     [:> Icon "expand_more"])}
      [:> Typography
       (-> platform :name)]]
     [:> ExpansionPanelDetails
      [:form {:style {:width "100%"}}
       [:> FormControl
        {:full-width true}
        [:> InputLabel
         {:html-for (str "platform-selector-" platformIdx)}
         "活動場所"]
        [:> Select
         {:value (-> platform :id)
          :onChange handleChangePlatform
          :full-width true
          :input-props #js {"name" "platform-selector"
                            "id" (str "platform-selector-" platformIdx)}}
         (map (fn [{:keys [id
                           name] :as option}]
                ^{:key id}
                [:> MenuItem
                 {:value id}
                 name])
              platformOptions)]
        [:> TextField
         {:label "ID"
          :margin "dense"
          :type "text"
          :full-width true
          :on-change handleChangePlatformUserId
          :default-value (:platformUserId platform)
          :placeholder (platform->placeholder
                        platform
                        platformOptions)}]]]]
     [:> ExpansionPanelActions
      [:> IconButton
       {:on-click handleClickDelete}
       [:> Icon
        "delete"]]]]))
