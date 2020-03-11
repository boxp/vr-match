(ns vr-match.setting.containers.top
  (:require
   [reagent.core :as r]
   [re-frame.core :as re-frame]
   [vr-match.events :as events]
   [vr-match.setting.components.top :as components]
   [vr-match.util :as util]))

(defn- handle-click-third-party-setting []
  (re-frame/dispatch [::events/push "/setting/third-party-authorization"]))

(defn top
  [_]
  [components/top {:handleClickThirdPartySetting handle-click-third-party-setting}])

(util/universal-set-loaded! :setting-top)
