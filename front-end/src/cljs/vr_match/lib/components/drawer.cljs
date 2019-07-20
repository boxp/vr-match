(ns vr-match.lib.components.drawer
  (:require
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.lib.components.elevation :as elevation]))

(defn drawer
  [{:keys [isOpen
           me
           handleClickClose
           handleClickMyProfile
           handleClickSearch
           handleClickFavorite
           handleClickMatching] :as props}]
  [mui/drawer {:open isOpen
               :onClose handleClickClose
               :style {:z-index elevation/drawer}}
   [:div {:style {:padding-top "56px"}}
    [mui/list
     [mui/list-item {:key "my-profile"
                     :button true
                     :on-click handleClickMyProfile}
      [mui/list-item-avatar
       [mui/avatar {:alt (:name me)
                    :src (-> me :images first :url)}]]
      [mui/list-item-text {:primary (:name me)}]]
     [mui/divider]
     [mui/list-item {:key "search"
                     :button true
                     :on-click handleClickSearch}
      [mui/list-item-icon
       [mui/icon "search"]]
      [mui/list-item-text {:primary "さがす"}]]
     [mui/list-item {:key "favorite"
                     :button true
                     :on-click handleClickFavorite}
      [mui/list-item-icon
       [mui/icon "favorite"]]
      [mui/list-item-text {:primary "お気に入り"}]]
     [mui/list-item {:key "matching"
                     :button true
                     :on-click handleClickMatching}
      [mui/list-item-icon
       [mui/icon "people"]]
      [mui/list-item-text "マッチング"]]]]])
