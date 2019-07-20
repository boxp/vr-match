(ns vr-match.lib.components.platform-link
  (:require [vr-match.lib.components.material-ui :as mui]))

(defn platform-link
  [{:keys [name url] :as platform}]
  [mui/button {:variant "contained"
               :color "primary"
               :size "small"
               :href url}
   [mui/icon {:style {:margin-right 8}} "link"]
   name])
