(ns vr-match.auth.components.email-register-complete
  (:require
   [reagent.core :as r]
   ["@material-ui/core" :as material-ui]
   [vr-match.lib.components.linear-progress :refer [linear-progress]]))

(defn- handle-change-email
  [draft-email e]
  (reset! draft-email (.. e -target -value)))

(defn- component-did-mount
  [this]
  (let [{:keys [handleInitialize] :as props} (r/props this)]
    (handleInitialize)))

(defn email-register-complete
  [_]
  (let [draft-email (r/atom "")]
    (r/create-class
     {:display-name "email-register-complete"
      :reagent-render
      (fn [{:keys [isLoading
                   handleSubmitEmail]}]
        [:<>
         (when isLoading [linear-progress])
         [:div {:style {:padding 16}}
          [:> material-ui/Grid {:container true
                                :spacing 32
                                :direction "column"
                                :justify "space-between"
                                :align-items "flex-start"
                                :style {:height "100%"
                                        :width "100vw"
                                        :padding 32}}
           [:> material-ui/Typography {:variant "display1"
                                       :component "h1"
                                       :gutterBottom true
                                       :style {:text-align "left"}}
            "メールアドレスをもう一度入力してください"]
           [:> material-ui/FormControl {:fullWidth true}
            [:> material-ui/TextField {:type "email"
                                       :auto-complete "email"
                                       :on-change #(handle-change-email draft-email %)
                                       :placeholder "sample@example.com"
                                       :disabled isLoading}]]
           [:> material-ui/Button {:disabled (or (= @draft-email "")
                                                 isLoading)
                                   :variant "contained"
                                   :color "primary"
                                   :on-click #(handleSubmitEmail @draft-email)
                                   :full-width true}
            "確認"]]]])
      :component-did-mount component-did-mount})))
