(ns vr-match.setting.components.top
  (:require
   [reagent.core :as r]
   [vr-match.lib.component :refer [navigation-bar-layout]]
   [vr-match.lib.components.material-ui :as mui]))

(defn top
  [{:keys [handleClickThirdPartySetting]}]
  [navigation-bar-layout {:title "各種設定"}
   [:div {:style {:position "relative"}}
    [mui/list {:subheader (r/as-element [mui/list-subheader "アカウント設定"])}
     [mui/list-item {:key "third-party-authorization-setting"
                     :on-click handleClickThirdPartySetting}
      [mui/list-item-text "認証設定"]
      [mui/list-item-secondary-action {:on-click handleClickThirdPartySetting}
       [mui/icon-button
        [mui/icon "navigate_next"]]]]]]])
