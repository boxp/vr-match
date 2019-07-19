(ns vr-match.favorite.component
  (:require
   [reagent.core :as r]
   [vr-match.lib.component :refer [navigation-bar-layout]]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.lib.components.user-list-item :refer [user-list-item]]))

(defn- component-did-mount
  [this]
  (let [props (r/props this)]
    ((:handleDidMount props))))

(def favorite-component
  (with-meta
    (fn
      [{:keys [items
               handleClickItem
               handleDidMount]}]
      [navigation-bar-layout {:title "お気に入りに登録したアバター"}
       [:div {:style {:padding "8px"}}
        [mui/list
         (map (fn [{:keys [id name platform images introduction]}]
                ^{:key id}
                [:div {:style {:margin-bottom "16px"}}
                 [user-list-item {:id id
                                  :image (->> images first :url)
                                  :platForms platforms
                                  :nickname name
                                  :introduction introduction
                                  :handleClick handleClickItem}]])
              items)]]])
    {:component-did-mount component-did-mount}))
