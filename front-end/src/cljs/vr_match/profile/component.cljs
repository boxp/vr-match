(ns vr-match.profile.component
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [clojure.string :as string]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.components.profile :as lib-profile]
            [vr-match.lib.component :refer [navigation-bar-layout]]))

(defn component-did-mount
  [this]
  ((:handleInitialize (r/props this))))

(def profile
  (with-meta
    (fn [{:keys [partner
                 handleInitialize] :as props}]
      [navigation-bar-layout {:title "プロフィール"}
       [lib-profile/profile
        (merge partner
               {:isShowPlatformLink (:isMatched partner)})]])
    {:component-did-mount component-did-mount}))

