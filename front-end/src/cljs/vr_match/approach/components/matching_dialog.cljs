(ns vr-match.approach.components.matching-dialog
  (:require [vr-match.lib.components.material-ui :as mui]))

(defn matching-dialog
  [{:keys [isOpen
           me
           partner
           handleClickGoToProfile
           handleClickBack] :as props}]
  [mui/dialog {:full-screen true
               :open isOpen
               :on-close handleClickBack
               :aria-labelledby "matching-dialog"}
   [mui/dialog-title {:variant "h1"
                      :component "h2"
                      :style {:margin-top "56px"
                              :text-align "center"}}
    "お相手とマッチングしました！"]
   [mui/dialog-content
    [mui/grid {:container true
               :direction "column"
               :justify "center"
               :align-items "center"
               :style {:margin-top "168"}}
     [mui/grid {:container true
                :direction "row"
                :justify "center"
                :align-items "center"}
      [mui/grid {:item true}
       [mui/slide {:direction "right"
                   :in isOpen}
        [mui/avatar {:alt (:name me)
                     :src (-> me :images first :url)
                     :style {:width 112
                             :height 112}}]]]
      [mui/grid {:item true}
       [mui/fade {:in isOpen}
        [mui/icon {:style {:margin "0 16px"}
                   :color "primary"}
         "favorite"]]]
      [mui/grid {:item true}
       [mui/slide {:direction "left"
                   :in isOpen}
        [mui/avatar {:alt (:name partner)
                     :src (-> partner :images first :url)
                     :style {:width 112
                             :height 112}}]]]]]]
   [mui/dialog-actions
    [mui/button {:on-click handleClickBack
                 :size "large"}
     "戻る"]
    [mui/button {:color "primary"
                 :size "large"
                 :on-click #(handleClickGoToProfile (:id partner))}
     "相手のプロフィールを見る"]]])
