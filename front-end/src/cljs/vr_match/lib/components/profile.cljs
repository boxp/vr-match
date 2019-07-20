(ns vr-match.lib.components.profile
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.component :refer [navigation-bar-layout]]
            [vr-match.lib.components.plat-form-chip :refer [plat-form-chip]]
            [vr-match.lib.components.platform-link :refer [platform-link]]))

(defn profile
  [{:keys [images
           platforms
           name
           introduction
           isShowPlatformLink] :as props}]
  [mui/grid {:container true
             :direction "column"
             :style {:width "100vw"}}
   [:img {:style {:object-fit "cover"
                  :width "100vw"
                  :height "100vw"}
          :src (-> images first :url)}]
   [:div {:style {:padding 16}}
    [mui/grid {:container true
               :spacing 16}
     [mui/grid {:item true
                :style {"marginBottom" "0.35em"}
                :xs 12}
      (if isShowPlatformLink
        [mui/grid {:container true
                   :justify "flex-start"
                   :spacing 8
                   :style {"marginBottom" 8}}
         (map (fn [{:keys [id platformUserId] :as platform}]
                [mui/grid {:key id
                           :item true}
                 (if platformUserId
                   [platform-link platform]
                   [plat-form-chip platform])])
              platforms)]
        [mui/grid {:container true
                   :justify "flex-start"
                   :spacing 8
                   :style {"marginBottom" 8}}
         (map (fn [{:keys [id name]}] [mui/grid {:key id
                                                 :item true}
                                       [plat-form-chip {:name name}]])
              platforms)])
      [mui/typo-graphy {:gutterBottom true
                        :variant "subheading"
                        :component "h2"}
       name]
      [mui/typo-graphy {:component "p"
                        :style {:word-break "break-word"}}
       introduction]]]]])
