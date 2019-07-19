(ns vr-match.matching.container
  (:require [reagent.core :as reagent]
            [vr-match.util :as util]
            [vr-match.matching.component :refer [matching-component]]
            [vr-match.matching.events :as matching-events]
            [vr-match.matching.subs :as matching-subs]
            [vr-match.events :as events]
            [re-frame.core :as re-frame]))

(defn handle-did-mount
  []
  (re-frame/dispatch [::matching-events/initialize]))

(defn handle-go-to-profile
  [id]
  (re-frame/dispatch [::events/push (str "/profile/" id)]))

(defn handle-fetch-next []
  (re-frame/dispatch [::matching-events/fetch-next-matching]))

(defn matching
  [params]
  (let [items (re-frame/subscribe [::matching-subs/matching-users])
        hasNext (re-frame/subscribe [::matching-subs/has-next-matching-users?])
        isLoading (re-frame/subscribe [::matching-subs/is-loading?])]
    (fn []
      [matching-component {:items @items
                           :hasNext @hasNext
                           :isLoading @isLoading
                           :handleDidMount handle-did-mount
                           :handleClickItem handle-go-to-profile
                           :handleFetchNext handle-fetch-next}])))

(util/universal-set-loaded! :matching)
