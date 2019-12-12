(ns vr-match.setting.containers.top
  (:require
   [reagent.core :as r]
   [vr-match.setting.components.top :as components]
   [vr-match.util :as util]))

(defn- handle-click-third-party-setting [])

(defn top
  [_]
  [components/top {:handleClickThirdPartySetting handle-click-third-party-setting}])

(util/universal-set-loaded! :setting-top)
