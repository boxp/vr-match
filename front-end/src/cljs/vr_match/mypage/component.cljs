(ns vr-match.mypage.component
  (:require
   [reagent.core :as r]
   [vr-match.lib.component :refer [navigation-bar-layout]]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.lib.components.linear-progress :refer [linear-progress]]
   [vr-match.mypage.components.edit-user-name-dialog :refer [edit-user-name-dialog]]
   [vr-match.mypage.components.edit-introduction-dialog :refer [edit-introduction-dialog]]
   [vr-match.mypage.components.edit-platform-dialog :refer [edit-platform-dialog]]))

(defn- handle-change-image
  [handle-submit-main-image e]
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
                     handle-submit-main-image)))
    (when file
      (.readAsDataURL reader file))))

(defn- handle-click-reset-image
  [image-ref]
  (some-> @image-ref .click))

(defn- platforms->text
  [platforms]
  (if (zero? (count platforms))
    "未設定"
    (->> platforms
         (map :name)
         (clojure.string/join ", "))))

(defn mypage
  [_]
  (let [image-ref (r/atom nil)
        editing-user-name? (r/atom false)
        editing-introduction? (r/atom false)
        editing-platform? (r/atom false)
        handle-click-user-name #(reset! editing-user-name? true)
        handle-close-user-name #(reset! editing-user-name? false)
        handle-click-introduction #(reset! editing-introduction? true)
        handle-close-introduction #(reset! editing-introduction? false)
        handle-click-platform #(reset! editing-platform? true)
        handle-close-platform #(reset! editing-platform? false)]
    (r/create-class
     {:display-name "mypage"
      :component-did-mount
      (fn [this]
        ((:handleInitialize (r/props this))))
      :reagent-render
      (fn [{:keys [me
                   platformOptions
                   isLoading
                   handleInitialize
                   handleSubmitUserName
                   handleSubmitIntroduction
                   handleSubmitMainImage]
            :as props}]
        [navigation-bar-layout {:title "プロフィールを編集"}
         [:div {:style {:position "relative"}}
          (when isLoading
            [linear-progress])
          [mui/button-base {:on-click #(handle-click-reset-image image-ref)
                            :style {:width "100vw"
                                    :height "100vw"}}
           [:div {:style {:width "100vw"
                          :height "100vw"
                          :background-image (str "url(" (some-> props :me :images first :url) ")")
                          :background-size "cover"
                          :background-position "center"}}]]
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
            [mui/list-item-text (:name me)]
            [mui/list-item-secondary-action {:on-click handle-click-user-name}
             [mui/icon-button
              [mui/icon "navigate_next"]]]]]
          [mui/list {:subheader (r/as-element [mui/list-subheader "自己紹介"])}
           [mui/list-item {:key "introduction"
                           :on-click handle-click-introduction}
            [mui/list-item-avatar
             [mui/avatar
              [mui/icon "notes"]]]
            [mui/list-item-text {:primary-typography-props #js {"noWrap" true}}
             (:introduction me)]
            [mui/list-item-secondary-action {:on-click handle-click-introduction}
             [mui/icon-button
              [mui/icon "navigate_next"]]]]]
          [mui/list {:subheader (r/as-element [mui/list-subheader "活動場所"])}
           [mui/list-item {:key "platform"
                           :on-click handle-click-platform}
            [mui/list-item-avatar
             [mui/avatar
              [mui/icon "place"]]]
            [mui/list-item-text {:primary-typography-props #js {"noWrap" true}}
             (-> me :platforms platforms->text)]
            [mui/list-item-secondary-action {:on-click handle-click-introduction}
             [mui/icon-button
              [mui/icon "navigate_next"]]]]]
          [:input {:type "file"
                   :on-change #(handle-change-image handleSubmitMainImage %)
                   :ref (fn [com] (reset! image-ref com))
                   :style {:display "none"}}]
          [edit-user-name-dialog {:isOpen @editing-user-name?
                                  :userName (-> me :name)
                                  :handleSubmit (fn [user-name]
                                                  (handleSubmitUserName user-name)
                                                  (handle-close-user-name))
                                  :handleCancel handle-close-user-name}]
          [edit-introduction-dialog {:isOpen @editing-introduction?
                                     :introduction (-> me :introduction)
                                     :handleSubmit (fn [introduction]
                                                     (handleSubmitIntroduction introduction)
                                                     (handle-close-introduction))
                                     :handleCancel handle-close-introduction}]
          [edit-platform-dialog {:isOpen @editing-platform?
                                 :platforms (-> me :platForms)
                                 :platformOptions platformOptions
                                 :handleClickSubmit handle-close-platform
                                 :handleCancel handle-close-platform}]]])})))
