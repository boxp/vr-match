(ns vr-match.auth.components.email-register
  (:require [reagent.core :as r]
            ["material-ui"]))

(defn- handle-change-email
  [draft-email e]
  (reset! draft-email (.. e -target -value)))

(defn- component-did-mount
  [this]
  (let [{:keys [handleInitialize] :as props} (r/props this)]
    (handleInitialize)))

(defn email-register
  [_]
  (let [draft-email (r/atom "")]
    (r/create-class
     {:display-name "email-register"
      :reagent-render
      (fn [{:keys [handleSubmit]}]
        [:div {:style {:padding 16}}
         [:> js/MaterialUI.Grid {:container true
                                 :spacing 32
                                 :direction "column"
                                 :justify "space-between"
                                 :align-items "flex-start"
                                 :style {:height "100%"
                                         :width "100vw"
                                         :padding 32}}
          [:> js/MaterialUI.Typography {:variant "display1"
                                        :component "h1"
                                        :gutterBottom true
                                        :style {:text-align "left"}}
           "メールアドレスを入力してください"]
          [:> js/MaterialUI.FormControl {:fullWidth true}
           [:> js/MaterialUI.TextField {:type "email"
                                        :auto-complete "email"
                                        :on-change #(handle-change-email draft-email %)
                                        :placeholder "sample@example.com"}]]
          [:> js/MaterialUI.Button {:variant "contained"
                                    :color "primary"
                                    :on-click #(handleSubmit @draft-email)
                                    :full-width true}
           "次へ"]]])
      :component-did-mount component-did-mount})))
