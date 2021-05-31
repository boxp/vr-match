(ns vr-match.approach.components.swipe-card-item
  (:require [reagent.core :as r]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.components.elevation :as elevation]
            [vr-match.lib.components.plat-form-chip :refer [plat-form-chip]]))

(defn swipe-card-item
  [{:keys [item
           handleClickCard]
    :as props}]
  (let [{:keys [id
                title
                name
                introduction
                platforms
                images]}
        (js->clj item :keywordize-keys true)]
    [mui/card {:style {"width" "100%"
                       "height" "100%"
                       "position" "relative"}}
     [mui/card-media {:style {"objectFit" "cover"
                              "width" "100%"
                              "height" "60%"
                              "flexGrow" 2}
                      :component "div"
                      :alt title
                      :image (-> images first :url)
                      :title title}]
     [mui/card-content {:style {"width" "100%"
                                "boxSizing" "border-box"
                                "flexGrow" 1}}
      [mui/icon-button {:style {:width "64px"
                                :height "64px"
                                :margin-top "-88px"
                                :margin-left "auto"
                                :margin-right "-24px"
                                :margin-botton "16px"
                                :display "block"
                                :box-sizing "unset"}
                        :on-click #(handleClickCard id)}
       [mui/icon {:font-size "inherit"
                  :style {:font-size "40px"}}
        "info"]]
      [mui/grid {:container true
                 :justify "flex-start"
                 :style {"marginBottom" "0.35em"}
                 :spacing 8}
       (->> platforms
            (take 3)
            (map (fn [{:keys [id name]}] [mui/grid {:key id
                                               :item true}
                                     [plat-form-chip {:name name}]])))]
      [mui/typo-graphy {:gutterBottom true
                        :variant "subheading"
                        :component "h2"}
       name]
      [mui/typo-graphy {:noWrap true}
       introduction]]]))

