(ns vr-match.setting.components.third-party-authorization
  (:require [reagent.core :as r]
            [vr-match.lib.component :refer [navigation-bar-layout]]
            [vr-match.lib.components.linear-progress :refer [linear-progress]]
            [vr-match.lib.components.material-ui :as mui]))

(defn third-party-authorization
  [_]
  (r/create-class
   {:display-name "third-party-authorization"
    :component-did-mount (fn [this]
                           ((:handleInitialize (r/props this))))
    :reagent-render
    (fn [{:keys [isLoading
                 isTwitterEnabled
                 handleChangeTwitter]}]
      [navigation-bar-layout {:title "外部サービス認証設定"}
       [:div {:style {:position "relative"}}
        (when isLoading
          [linear-progress])
        [mui/list
         [mui/list-item {:key "twitter"
                         :on-click handleChangeTwitter}
          [mui/list-item-text "Twitter"]
          [mui/list-item-secondary-action
           [mui/switch {:edge "end"
                        :disabled isLoading
                        :checked isTwitterEnabled
                        :onChange handleChangeTwitter}]]]]]])}))
