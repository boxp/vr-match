(ns vr-match.favorited-from-users.container
  (:require [reagent.core :as reagent]
            [vr-match.util :as util]
            [vr-match.favorited-from-users.component :refer [favorited-from-users-component]]
            [vr-match.favorited-from-users.events :as favorited-from-users-events]
            [vr-match.favorited-from-users.subs :as favorited-from-users-subs]
            [vr-match.events :as events]
            [re-frame.core :as re-frame]))

(defn handle-did-mount
  []
  (re-frame/dispatch [::favorited-from-users-events/initialize]))

(defn handle-go-to-profile
  [id]
  (re-frame/dispatch [::events/push (str "/profile/" id)]))

(defn handle-fetch-next []
  (re-frame/dispatch [::favorited-from-users-events/fetch-next-favorited-from-users]))

(defn favorited-from-users
  [params]
  (let [items (re-frame/subscribe [::favorited-from-users-subs/favorited-from-users])
        hasNext (re-frame/subscribe [::favorited-from-users-subs/has-next-favorited-users?])
        isLoading (re-frame/subscribe [::favorited-from-users-subs/is-loading?])
        isFetched (re-frame/subscribe [::favorited-from-users-subs/is-fetched?])]
    (fn []
      [favorited-from-users-component {:items @items
                           :hasNext @hasNext
                           :isLoading @isLoading
                           :isFetched @isFetched
                           :handleDidMount handle-did-mount
                           :handleClickItem handle-go-to-profile
                           :handleFetchNext handle-fetch-next}])))

(util/universal-set-loaded! :favorited-from-users)
