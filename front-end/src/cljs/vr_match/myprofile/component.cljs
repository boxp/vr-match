(ns vr-match.myprofile.component
  (:require
   [reagent.core :as r]
   [vr-match.lib.component :refer [navigation-bar-layout]]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.lib.components.profile :refer [profile]]))

(defn component-did-mount
  [this]
  ((:handleInitialize (r/props this))))

(def myprofile
  (with-meta
    (fn [{:keys [me
                 handleClickEditMyProfile] :as props}]
      [navigation-bar-layout {:title "プロフィール"}
       [:div {:style {:position "relative"}}
        [profile (merge me {:isShowPlatformLink true})]
        [:div {:style {:position "fixed"
                       :right 0
                       :left 0
                       :bottom "16px"
                       :padding "0 16px"}}
         [mui/button {:variant "contained"
                      :color "primary"
                      :style {:width "100%"}
                      :on-click handleClickEditMyProfile}
          [mui/icon {:style {:margin-right "8px"}}
           "edit"]
          "プロフィールを編集"]]]])
    {:component-did-mount component-did-mount}))
