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

(defn favorite
  [params]
  (let [items (re-frame/subscribe [::favorite-subs/favorited-users-from-me])]
    (fn []
      [favorite-component (merge {:items @items
                                  :handleDidMount handle-did-mount
                                  :handleClickItem handle-go-to-profile})])))

(util/universal-set-loaded! :favorite)
