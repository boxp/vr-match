(ns vr-match.favorite.container
  (:require [reagent.core :as reagent]
            [vr-match.util :as util]
            [vr-match.favorite.component :refer [favorite-component]]
            [vr-match.favorite.events :as favorite-events]
            [vr-match.favorite.subs :as favorite-subs]
            [vr-match.events :as events]
            [re-frame.core :as re-frame]))

(defn handle-did-mount
  []
  (re-frame/dispatch [::favorite-events/initialize]))

(defn handle-go-to-profile
  [id]
  (re-frame/dispatch [::events/push (str "/profile/" id)]))

(defn handle-fetch-next []
  (re-frame/dispatch [::favorite-events/fetch-next-favorited-from-me]))

(defn favorite
  [params]
  (let [items (re-frame/subscribe [::favorite-subs/favorited-users-from-me])
        hasNext (re-frame/subscribe [::favorite-subs/has-next-favorited-users?])
        isLoading (re-frame/subscribe [::favorite-subs/is-loading?])]
    (fn []
      [favorite-component {:items @items
                           :hasNext @hasNext
                           :isLoading @isLoading
                           :handleDidMount handle-did-mount
                           :handleClickItem handle-go-to-profile
                           :handleFetchNext handle-fetch-next}])))

(util/universal-set-loaded! :favorite)
