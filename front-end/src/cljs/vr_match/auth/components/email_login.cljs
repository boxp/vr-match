(ns vr-match.auth.components.email-login
  (:require
   [reagent.core :as r]
   ["@material-ui/core/Grid" :as Grid]
   ["@material-ui/core/Typography" :as Typography]
   ["@material-ui/core/Icon" :as Icon]
   ["@material-ui/core/Button" :as Button]
   ["@material-ui/core/FormControl" :as FormControl]
   ["@material-ui/core/TextField" :as TextField]
   [vr-match.lib.components.linear-progress :refer [linear-progress]]))

(defn- handle-change-email
  [draft-email e]
  (reset! draft-email (.. e -target -value)))

(defn- component-did-mount
  [this]
  (let [{:keys [handleInitialize] :as props} (r/props this)]
    (handleInitialize)))

(defn send-email-sign-in-link-complete
  [email]
  [:div {:style {:padding 16}}
   [:> Grid {:container true
             :spacing 32
             :direction "column"
             :justify "space-between"
             :align-items "flex-start"
             :style {:height "100%"
                     :width "100vw"
                     :padding 32}}
    [:> Typography {:variant "display1"
                    :component "h1"
                    :gutterBottom true
                    :style {:text-align "left"}}
     "ログイン確認メールを送信しました"]
    [:> Grid {:container true
              :spacing 32
              :direction "column"
              :justify "space-between"
              :align-items "center"
              :style {:padding 32}}
     [:> Icon
      {:font-size "inherit"
       :color "primary"
       :style {:font-size "10em"}}
     "mail_outline"]]
    [:> Typography {:variant "body1"
                    :component "p"}
     [:span {:style {:font-weight "bold"}}
      email]
     " へ届いた確認用リンクを確認してください"]]])

(defn email-login
  [_]
  (let [draft-email (r/atom "")]
    (r/create-class
     {:display-name "email-login"
      :reagent-render
      (fn [{:keys [isCompletedSendLink
                   isLoading
                   sentEmail
                   handleSubmit]}]
        (if isCompletedSendLink
          [send-email-sign-in-link-complete sentEmail]
          [:<>
           (when isLoading [linear-progress])
            [:div {:style {:padding 16}}
             [:> Grid {:container true
                       :spacing 32
                       :direction "column"
                       :justify "space-between"
                       :align-items "flex-start"
                       :style {:height "100%"
                               :width "100vw"
                               :padding 32}}
              [:> Typography {:variant "display1"
                              :component "h1"
                              :gutterBottom true
                              :style {:text-align "left"}}
               "メールアドレスを入力してください"]
              [:> FormControl {:fullWidth true}
               [:> TextField {:type "email"
                              :auto-complete "email"
                              :on-change #(handle-change-email draft-email %)
                              :placeholder "sample@example.com"}]]
              [:> Button {:disabled (= @draft-email "")
                          :variant "contained"
                          :color "primary"
                          :on-click #(handleSubmit @draft-email)
                          :full-width true}
               "次へ"]]]]))
      :component-did-mount component-did-mount})))
