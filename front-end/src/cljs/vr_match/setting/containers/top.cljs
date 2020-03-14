(ns vr-match.setting.containers.top
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [vr-match.events :as events]
   [vr-match.setting.components.top :as components]
   [vr-match.util :as util]))

(defn- handle-click-third-party-setting []
  (re-frame/dispatch [::events/push "/setting/third-party-authorization"]))

(defn- handle-initialize []
  (re-frame/dispatch [::events/fetch-me]))

(defn top
  [_]
  (r/create-class
   {:display-name "top"
    :component-did-mount handle-initialize
    :reagent-render
    (fn [_]
      [components/top {:handleClickThirdPartySetting handle-click-third-party-setting}])}))

(util/universal-set-loaded! :setting-top)
