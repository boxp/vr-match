(ns vr-match.approach.components.swipe-card-item
  (:require [reagent.core :as r]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.components.plat-form-chip :refer [plat-form-chip]]))

(defn swipe-card-item
  [{:keys [item
           handleClickCard]
    :as props}]
  (let [{:keys [id
                title
                userName
                introduction
                platforms
                image]}
        (js->clj item :keywordize-keys true)]
     [mui/card {:style {"width" "86vw"
                        "height" "64vh"
                        "position" "relative"}}
      [mui/card-action-area {:style {"width" "86vw"
                                     "height" "100%"
                                     "position" "relative"
                                     "display" "flex"
                                     "flexDirection" "column"}
                             :disableRipple true
                             :on-click #(handleClickCard id)}
       [mui/card-media {:style {"objectFit" "cover"
                                "width" "86vw"
                                "height" 284
                                "flexGrow" 2}
                        :component "div"
                        :alt title
                        :image (first image)
                        :title title}]
       [mui/card-content {:style {"width" "100%"
                                  "boxSizing" "border-box"
                                  "flexGrow" 1}}
        [mui/grid {:container true
                   :justify "flex-start"
                   :style {"marginBottom" "0.35em"}
                   :spacing 8}
         (map (fn [{:keys [id name]}] [mui/grid {:key id
                                                 :item true}
                                       [plat-form-chip {:name name}]])
              platforms)]
        [mui/typo-graphy {:gutterBottom true
                          :variant "subheading"
                          :component "h2"}
         userName]
        [mui/typo-graphy {:noWrap true
                          :style {:width "72vw"}}
         introduction]]]]))

