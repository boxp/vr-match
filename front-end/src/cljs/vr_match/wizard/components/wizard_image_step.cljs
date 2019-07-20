(ns vr-match.wizard.components.wizard-image-step
  (:require
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.lib.components.linear-progress :refer [linear-progress]]
   [vr-match.wizard.components.wizard-step :refer [wizard-step]]
   [vr-match.wizard.components.wizard-title :refer [wizard-title]]))

(defn wizard-image-step
  [{:keys [me
           handleClickNext
           handleResetImage]}]
  (let [draft-image (r/atom nil)
        image-ref (r/atom nil)
        handle-change-image
        (fn [e]
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
            (.readAsDataURL reader file)))
        handle-click-next (fn [] (handleClickNext @draft-image))
        handle-click-reset-image (fn [e]
                                   (some-> @image-ref .click))]
    (fn [{:keys [isLoading]}]
      [mui/fade {:in true}
       [:div {:style {:padding 16}}
        (when isLoading
          [linear-progress])
        [mui/grid {:container true
                   :spacing 32
                   :direction "column"
                   :justify "space-between"
                   :align-items "flex-start"
                   :style {:height "100%"
                           :width "100vw"
                           :padding 32}}
         [wizard-title {:title "プロフィール画像を設定しましょう"}]
         [mui/grid {:container true
                    :justify "center"
                    :align-items "center"}
          [:div {:style {:width "240px"
                         :height "240px"
                         :position "relative"}}
           [mui/button-base {:on-click handle-click-reset-image
                             :style {:border-radius "100%"}}
            [mui/avatar {:src @draft-image
                         :style {:width "240px"
                                 :height "240px"}}]]
           [mui/icon-button {:on-click handle-click-reset-image
                             :style
                             {:position "absolute"
                              :width "64px"
                              :height "64px"
                              :bottom "8px"
                              :right "8px"
                              :background-color mui/primary-color}}
            [mui/icon {:font-size "inherit"
                       :style {:color "white"
                               :font-size "32px"}}
             "edit"]]]]
         [mui/grid {:container true
                    :direction "column"}
          [mui/button {:disabled (nil? @draft-image)
                       :variant "contained"
                       :color "primary"
                       :on-click handle-click-next}
           "次へ"]
          [:input {:type "file"
                   :id "js-select-file"
                   :on-change handle-change-image
                   :ref (fn [com] (reset! image-ref com))
                   :style {:display "none"}}]]]]])))
