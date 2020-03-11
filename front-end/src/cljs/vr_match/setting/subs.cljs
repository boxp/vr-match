(ns vr-match.setting.subs
  (:require
   [re-frame.core :as re-frame]
   [vr-match.auth.subs :as auth-subs]))

(re-frame/reg-sub
 ::third-party-authorization-loading?
 (fn []
   [(re-frame/subscribe [::auth-subs/linked-provider-ids-loaded?])])
 (fn [[linked-provider-ids-loaded?] _]
   (not linked-provider-ids-loaded?)))

