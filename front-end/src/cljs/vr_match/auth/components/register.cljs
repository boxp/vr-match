(ns vr-match.auth.components.register
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [clojure.string :as string]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.components.plat-form-chip :refer [plat-form-chip]]))

(defn register
  [{:keys [backgroundImage
           handleClickTwitter
           handleClickEmailRegister] :as props}]
  [mui/grid {:style {:height "100%"
                     :width "100%"}
             :container true
             :direction "column"
             :justify "center"
             :alignItems "center"}
   [:span {:style {:display "block"
                   :height "100%"
                   :width "100%"
                   :background (str "url(" backgroundImage ") no-repeat center center fixed")
                   :background-size "cover"
                   :filter "blur(4px) opacity(64%)"
                   :position "absolute"
                   :top 0
                   :left 0
                   :right 0
                   :bottom 0
                   :z-index -1}}]
   [mui/typo-graphy {:component "h1"
                     :gutterBottom true
                     :variant "display3"}
    "Hito Hub"]
   [mui/grid {:style {:margin-top 208
                      :width "296px"}
              :container true
              :direction "column"
              :justify "center"
              :alignItems "center"}
    [mui/button {:disabled true
                 :variant "contained"
                 :color "primary"
                 :on-click handleClickTwitter
                 :style {:width "100%"}}
     "Twitterアカウントで登録"]
    [mui/button {:variant "contained"
                 :color "primary"
                 :on-click handleClickEmailRegister
                 :style {:width "100%"
                         :margin-top "16px"}}
     "Emailで登録"]]])
