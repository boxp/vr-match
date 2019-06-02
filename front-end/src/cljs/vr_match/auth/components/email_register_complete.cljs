(ns vr-match.auth.components.email-register-complete
  (:require
   [reagent.core :as r]))

(defn- component-did-mount
  [this]
  (let [{:keys [handleInitialize] :as props} (r/props this)]
    (handleInitialize)))

(defn email-register-complete
  [_]
  (r/create-class
   {:display-name "email-register-complete"
    :reagent-render
    (fn []
      [:div])
    :component-did-mount component-did-mount}))
