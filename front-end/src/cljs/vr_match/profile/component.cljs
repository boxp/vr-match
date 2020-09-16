(ns vr-match.profile.component
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [clojure.string :as string]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.components.linear-progress :refer [linear-progress]]
            [vr-match.lib.components.profile :as lib-profile]
            [vr-match.lib.component :refer [navigation-bar-layout]]
            [vr-match.approach.components.matching-dialog :refer [matching-dialog]]))

(defn component-did-mount
  [this]
  ((:handleInitialize (r/props this))))

(def profile
  (with-meta
    (fn [{:keys [isLoading
                 isShowMatchingDialog
                 me
                 partner
                 handleInitialize
                 handleClickFavorite
                 handleCloseMatchingDialog] :as props}]
      (let [matched? (:isMatched partner)]
        [navigation-bar-layout {:title "プロフィール"}
         [:<>
          (when isLoading
            [linear-progress])
          [:div {:style {:margin-bottom (if matched? "0px" "36px")}}
           [lib-profile/profile
            (merge partner
                   {:isShowPlatformLink (:isMatched partner)})]]
          (when-not matched?
            [:div {:style {:position "fixed"
                           :right 0
                           :left 0
                           :bottom "16px"
                           :padding "0 16px"}}
             [mui/button {:variant "contained"
                          :color "primary"
                          :style {:width "100%"}
                          :on-click handleClickFavorite}
              [mui/icon {:style {:margin-right "8px"}} "favorite"]
              "お気に入り"]])
          [matching-dialog {:isOpen isShowMatchingDialog
                            :me me
                            :partner partner
                            :handleClickGoToProfile handleCloseMatchingDialog
                            :handleClickBack handleCloseMatchingDialog}]]]))
    {:component-did-mount component-did-mount}))

