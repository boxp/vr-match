(ns vr-match.wizard.component
  (:require
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.wizard.components.wizard-nickname-step :refer [wizard-nickname-step]]
   [vr-match.wizard.components.wizard-platform-step :refer [wizard-platform-step]]
   [vr-match.wizard.components.wizard-image-step :refer [wizard-image-step]]))

(def wizard
  (r/create-class
   {:display-name "wizard"
    :component-did-mount
    (fn [this]
      ((:handleInitialize (r/props this))))
    :reagent-render
    (fn [{:keys [me
                 step
                 platformChoices
                 handleInitialize
                 handleNextNicknameStep
                 handleNextPlatformStep
                 handleNextImageStep] :as props}]
      (case step
        :nickname [wizard-nickname-step {:me me
                                         :handleClickNext handleNextNicknameStep}]
        :platform [wizard-platform-step {:me me
                                         :platformChoices platformChoices
                                         :handleClickNext handleNextPlatformStep}]
        :image [wizard-image-step {:me me
                                   :handleClickNext handleNextImageStep}]
        nil))}))
