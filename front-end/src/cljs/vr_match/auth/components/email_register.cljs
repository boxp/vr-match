(ns vr-match.auth.components.email-register
  (:require [reagent.core :as r]
            ["@material-ui/core" :as material-ui]
            [vr-match.lib.components.linear-progress :refer [linear-progress]] ))

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
     "登録確認メールを送信しました"]
    [:> material-ui/Grid {:container true
                          :spacing 32
                          :direction "column"
                          :justify "space-between"
                          :align-items "center"
                          :style {:padding 32}}
     [:> material-ui/Icon
      {:font-size "inherit"
       :color "primary"
       :style {:font-size "10em"}}
      "mail_outline"]]
    [:> material-ui/Typography {:variant "body1"
                                :component "p"}
     [:span {:style {:font-weight "bold"}}
      email]
     " へ届いた確認用リンクを確認してください"]]])

(defn email-register
  [_]
  (let [draft-email (r/atom "")]
    (r/create-class
     {:display-name "email-register"
      :reagent-render
      (fn [{:keys [isCompletedSendLink
                   isLoading
                   sentEmail
                   handleSubmit]}]
        (if isCompletedSendLink
          [send-email-sign-in-link-complete sentEmail]
          [:<>
           (when isLoading
             [linear-progress])
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
              "メールアドレスを入力してください"]
             [:> material-ui/FormControl {:fullWidth true}
              [:> material-ui/TextField {:type "email"
                                         :auto-complete "email"
                                         :on-change #(handle-change-email draft-email %)
                                         :placeholder "sample@example.com"}]]
             [:> material-ui/Button {:disabled (= @draft-email "")
                                     :variant "contained"
                                     :color "primary"
                                     :on-click #(handleSubmit @draft-email)
                                     :full-width true}
              "次へ"]]]]))
      :component-did-mount component-did-mount})))
