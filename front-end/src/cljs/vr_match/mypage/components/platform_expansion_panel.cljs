(ns vr-match.mypage.components.platform-expansion-panel
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [vr-match.lib.models.platform :as platform-model]
            ["@material-ui/core" :as material-ui]))

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
    [:> material-ui/ExpansionPanel
     [:> material-ui/ExpansionPanelSummary
      {:expand-icon (r/as-element
                     [:> material-ui/Icon "expand_more"])}
      [:> material-ui/Typography
       (-> platform :name)]]
     [:> material-ui/ExpansionPanelDetails
      [:form {:style {:width "100%"}}
       [:> material-ui/FormControl
        {:full-width true}
        [:> material-ui/InputLabel
         {:html-for (str "platform-selector-" platformIdx)}
         "活動場所"]
        [:> material-ui/Select
         {:value (-> platform :id)
          :onChange handleChangePlatform
          :full-width true
          :input-props #js {"name" "platform-selector"
                            "id" (str "platform-selector-" platformIdx)}}
         (map (fn [{:keys [id
                           name] :as option}]
                ^{:key id}
                [:> material-ui/MenuItem
                 {:value id}
                 name])
              platformOptions)]
        [:> material-ui/TextField
         {:label "ID"
          :margin "dense"
          :type "text"
          :full-width true
          :on-change handleChangePlatformUserId
          :default-value (:platformUserId platform)
          :placeholder (platform->placeholder
                        platform
                        platformOptions)}]]]]
     [:> material-ui/ExpansionPanelActions
      [:> material-ui/IconButton
       {:on-click handleClickDelete}
       [:> material-ui/Icon
        "delete"]]]]))
