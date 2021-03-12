(ns vr-match.matching.component
  (:require
   [reagent.core :as r]
   [vr-match.lib.component :refer [navigation-bar-layout]]
   [vr-match.lib.components.linear-progress :refer [linear-progress]]
   [vr-match.lib.components.progress-button :refer [progress-button]]
   ["@material-ui/core" :as material-ui]
   [vr-match.lib.components.user-list-item :refer [user-list-item]]))

(defn- component-did-mount
  [this]
  (let [props (r/props this)]
    (when-not (:isFetched props)
      ((:handleDidMount props)))))

(def matching-component
  (with-meta
    (fn
      [{:keys [items
               hasNext
               isLoading
               handleClickItem
               handleDidMount
               handleFetchNext]}]
      [navigation-bar-layout {:title "マッチングしたアバター"}
       [:div {:style {:padding "8px"}}
        (when isLoading
          [linear-progress])
        [:> material-ui/List
         (map (fn [{:keys [id name platforms images introduction]}]
                ^{:key id}
                [:div {:style {:margin-bottom "16px"}}
                 [user-list-item {:id id
                                  :image (->> images first :url)
                                  :platForms platforms
                                  :nickname name
                                  :introduction introduction
                                  :handleClick handleClickItem}]])
              items)]
        (when hasNext
          [progress-button {:loading? isLoading
                            :color "primary"
                            :variant "contained"
                            :full-width true
                            :on-click handleFetchNext}
           "もっと見る"])]])
    {:component-did-mount component-did-mount}))
