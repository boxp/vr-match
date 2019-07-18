(ns vr-match.wizard.components.wizard-image-step
  (:require
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]
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
    (fn []
      [mui/fade {:in true}
       [:div {:style {:padding 16}}
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
          [:a {:role "button"
               :style {:position "relative"}}
           [mui/avatar {:src @draft-image
                        :style {:width "240px"
                                :height "240px"}}]]]
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
                   :style {:display "none"}}]
          [mui/button {:style {:margin-top 16}
                       :on-click handle-click-reset-image}
           "画像を再設定する"]]]]])))
