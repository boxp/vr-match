(ns vr-match.setting.components.third-party-authorization
  (:require [reagent.core :as r]
            [vr-match.setting.components.unlink-confirmation-alert :refer [unlink-confirmation-alert]]
            [vr-match.lib.component :refer [navigation-bar-layout]]
            [vr-match.lib.components.linear-progress :refer [linear-progress]]
            [vr-match.lib.components.material-ui :as mui]))

(defn third-party-authorization
  [_]
  (let [unlinking-provider-id (r/atom nil)
        handle-unlink-twitter (fn [] (reset! unlinking-provider-id "twitter.com"))
        handle-unlink-email (fn [] (reset! unlinking-provider-id "password"))
        handle-close-unlink-confirmation-alert (fn [] (reset! unlinking-provider-id nil))]
    (r/create-class
     {:display-name "third-party-authorization"
      :component-did-mount (fn [this]
                             ((:handleInitialize (r/props this))))
      :reagent-render
      (fn [{:keys [isLoading
                   isTwitterEnabled
                   isEmailEnabled
                   handleLinkTwitter
                   handleUnlinkTwitter
                   handleUnlinkEmail]}]
        [:<>
         [navigation-bar-layout {:title "認証設定"}
          [:div {:style {:position "relative"}}
           (when isLoading
             [linear-progress])
           [mui/list
            [mui/list-item {:key "twitter"}
             [mui/list-item-text "Twitter認証"]
             [mui/list-item-secondary-action
              [mui/switch {:edge "end"
                           :disabled isLoading
                           :checked isTwitterEnabled
                           :onChange (if isTwitterEnabled
                                       handle-unlink-twitter
                                       handleLinkTwitter)}]]]
            [mui/list-item {:key "email"}
             [mui/list-item-text "メール認証"]
             [mui/list-item-secondary-action
              [mui/switch {:edge "end"
                           :disabled (or isLoading (not isEmailEnabled))
                           :checked isEmailEnabled
                           :onChange (when isEmailEnabled
                                       handle-unlink-email)}]]]]]]
         [unlink-confirmation-alert {:isOpen (not (nil? @unlinking-provider-id))
                                     :thirdPartyId @unlinking-provider-id
                                     :handleClose handle-close-unlink-confirmation-alert
                                     :handleSubmit (case @unlinking-provider-id
                                                     "twitter.com" handleUnlinkTwitter
                                                     "password" handleUnlinkEmail
                                                     #())}]])})))
