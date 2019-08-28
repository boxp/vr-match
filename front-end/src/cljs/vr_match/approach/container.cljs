(ns vr-match.approach.container
  (:require [reagent.core :as reagent]
            [vr-match.approach.component :as component]
            [vr-match.approach.events :as approach-events]
            [vr-match.approach.subs :as approach-subs]
            [vr-match.util :as util]
            [vr-match.events :as events]
            [vr-match.subs :as subs]
            [re-frame.core :as re-frame]))

(declare mock-approach-state)

(defn handle-click-skip
  [id]
  (re-frame/dispatch [::approach-events/skip id]))

(defn handle-click-favorite
  [user]
  (re-frame/dispatch [::approach-events/favorite user]))

(defn handle-fetch-next []
  (re-frame/dispatch [::approach-events/fetch-next-approach-list]))

(defn handle-click-matching-dialog-back []
  (re-frame/dispatch [::approach-events/close-matching-dialog]))

(defn handle-did-mount
  []
  (re-frame/dispatch [::approach-events/initialize]))

(defn handle-click-go-to-profile
  [id]
  (re-frame/dispatch [::events/push (str "/profile/" id)]))

(defn handle-reset-all-skip []
  (re-frame/dispatch [::approach-events/reset-all-skip]))

(defn approach
  [params]
  (let [me (re-frame/subscribe [::subs/me])
        card-items (re-frame/subscribe [::approach-subs/approach-list])
        isShowMatchingDialog (re-frame/subscribe [::approach-subs/show-matching-dialog?])
        matchingPartner (re-frame/subscribe [::approach-subs/in-favorite-user])
        isLoading (re-frame/subscribe [::approach-subs/loading?])
        isLoaded (re-frame/subscribe [::approach-subs/loaded?])
        hasNextPage (re-frame/subscribe [::approach-subs/has-next-page?])]
    [component/approach {:me @me
                         :isShowMatchingDialog @isShowMatchingDialog
                         :isLoaded @isLoaded
                         :isLoading @isLoading
                         :hasNextPage @hasNextPage
                         :matchingPartner @matchingPartner
                         :cardItems @card-items
                         :handleClickSkip handle-click-skip
                         :handleClickFavorite handle-click-favorite
                         :handleClickGoToProfile handle-click-go-to-profile
                         :handleClickMatchingDialogBack handle-click-matching-dialog-back
                         :handleDidMount handle-did-mount
                         :handleFetchNext handle-fetch-next
                         :handleResetAllSkip handle-reset-all-skip}]))

(util/universal-set-loaded! :approach)
