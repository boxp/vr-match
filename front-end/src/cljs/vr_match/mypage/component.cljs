(ns vr-match.mypage.component
  (:require
   [reagent.core :as r]
   [vr-match.lib.component :refer [navigation-bar-layout]]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.mypage.components.edit-user-name :refer [edit-user-name]]))

(defn- handle-change-image
  [draft-image e]
  (let [file (some-> e
                     .-target
                     .-files
                     (aget 0))
        reader (js/FileReader.)]
    (set! (.-onload reader)
          (fn [event]
            (some->> event
                     .-target
                     .-result
                     (reset! draft-image))))
    (when file
      (.readAsDataURL reader file))))

(defn- handle-click-reset-image
  [image-ref]
  (some-> @image-ref .click))

(defn mypage
  [{:keys [me
           handleSubmitUserName] :as props}]
  (let [draft-image (r/atom (-> me :image first))
        image-ref (r/atom nil)
        editing-user-name? (r/atom false)
        handle-click-user-name #(reset! editing-user-name? true)
        handle-user-name-cancel #(reset! editing-user-name? false)]
    (fn []
      (cond
        @editing-user-name?
        [edit-user-name {:userName (:userName me)
                         :handleSubmit handleSubmitUserName
                         :handleCancel handle-user-name-cancel}]
        :default
        [navigation-bar-layout {:title "プロフィールを編集"}
         [:div {:style {:position "relative"}}
          [mui/button-base {:on-click #(handle-click-reset-image image-ref)
                            :style {:width "100vw"
                                    :height "100vw"}}
           [:img {:src @draft-image
                  :style {:width "100vw"
                          :height "100vw"}}]]
          [mui/icon-button {:on-click #(handle-click-reset-image image-ref)
                            :style
                            {:position "absolute"
                             :width "64px"
                             :height "64px"
                             :top "calc(100vw - 40px)"
                             :right "16px"
                             :background-color mui/primary-color}}
           [mui/icon {:font-size "large"
                      :style {:color "white"}}
            "edit"]]
          [mui/list {:subheader (r/as-element [mui/list-subheader "ユーザー名"])}
           [mui/list-item {:key "user-name"
                           :on-click handle-click-user-name}
            [mui/list-item-avatar
             [mui/avatar
              [mui/icon "person"]]]
            [mui/list-item-text (:userName me)]
            [mui/list-item-secondary-action {:on-click handle-click-user-name}
             [mui/icon-button
              [mui/icon "navigate_next"]]]]]
          [:input {:type "file"
                   :on-change #(handle-change-image draft-image %)
                   :ref (fn [com] (reset! image-ref com))
                   :style {:display "none"}}]]]))))
