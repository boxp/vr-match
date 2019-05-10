(ns vr-match.mypage.component
  (:require
   [reagent.core :as r]
   [vr-match.lib.component :refer [navigation-bar-layout]]
   [vr-match.lib.components.material-ui :as mui]))

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
           handleSubmit] :as props}]
  (let [draft-image (r/atom (-> me :image first))
        image-ref (r/atom nil)]
    (fn []
      [navigation-bar-layout {:title "プロフィールを編集"}
       [:<>
        [mui/button-base {:on-click #(handle-click-reset-image image-ref)
                          :style {:position "relative"
                                  :width "100vw"
                                  :height "100vw"}}
         [:img {:src @draft-image
                :style {:width "100vw"
                        :height "100vw"}}]
         [mui/avatar {:style {:position "absolute"
                              :width "56px"
                              :height "56px"
                              :top "16px"
                              :right "16px"
                              :background-color mui/primary-color}}
          [mui/icon {:font-size "large"}
           "edit"]]]
        [:input {:type "file"
                 :on-change #(handle-change-image draft-image %)
                 :ref (fn [com] (reset! image-ref com))
                 :style {:display "none"}}]]])))
