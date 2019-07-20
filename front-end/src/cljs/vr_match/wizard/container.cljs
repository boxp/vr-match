(ns vr-match.wizard.container
  (:require
   [clojure.string :as string]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [vr-match.util :as util]
   [vr-match.events :as events]
   [vr-match.subs :as subs]
   [vr-match.wizard.events :as wizard-events]
   [vr-match.wizard.subs :as wizard-subs]
   [vr-match.wizard.component :as component]))

;; TODO: re-frameとつなぎこんで消す
(def mock-wizard-state
  (reagent/atom {:step :nickname}))

(defn- handle-initialize []
  (re-frame/dispatch [::wizard-events/initialize]))

(defn- handle-next-nickname-step
  [nickname]
  (re-frame/dispatch [::wizard-events/update-me {:name nickname}])
  (swap! mock-wizard-state
         (fn [state]
           (assoc state :step :platform))))

(defn- handle-next-platform-step
  [platforms]
  (re-frame/dispatch
   [::wizard-events/update-me {:platforms
                               {:platforms
                                (->> platforms
                                     (map #(select-keys % [:id :name])))}}])
  (swap! mock-wizard-state
         (fn [state]
           (assoc state :step :image))))

(defn- handle-next-image-step
  [image]
  (re-frame/dispatch [::wizard-events/upload-image
                      {:base64-string (->> (string/split image #",")
                                           last)}])
  (re-frame/dispatch [::events/push "/approach"]))

(defn wizard
  [params]
  (let [me (re-frame/subscribe [::subs/me])
        platformChoices (re-frame/subscribe [::wizard-subs/platform-options])
        isLoading (re-frame/subscribe [::wizard-subs/loading?])]
    (fn [params]
      [component/wizard (merge @mock-wizard-state
                               {:me @me
                                :isLoading @isLoading
                                :platformChoices @platformChoices
                                :handleInitialize handle-initialize
                                :handleNextNicknameStep handle-next-nickname-step
                                :handleNextPlatformStep handle-next-platform-step
                                :handleNextImageStep handle-next-image-step})])))

(util/universal-set-loaded! :wizard)
