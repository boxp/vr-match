(ns vr-match.auth.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::sign-in-link-loading?
 (fn [db]
   (-> db :fetch-status :sign-in-link (= :loading))))

(re-frame/reg-sub
 ::send-sign-in-link-to-email-succeed?
 (fn [db]
   (and
    (-> db :fetch-status :sign-in-link (= :loaded))
    (-> db :auth :sign-in-link :error nil?))))

(re-frame/reg-sub
 ::get-send-sign-in-link-to-email-error
 (fn [db]
   (-> db :auth :sign-in-link :error)))

(re-frame/reg-sub
 ::get-sent-email
 (fn [db]
   (-> db :auth :sign-in-link :email)))

(re-frame/reg-sub
 ::loading-email?
 (fn [db]
   (or
     (-> db :fetch-status :sign-in-link (= :loading)))))

(re-frame/reg-sub
 ::loading-email-complete?
 (fn [db]
   (or
     (-> db :fetch-status :sign-in-with-email (= :loading))
     (-> db :fetch-status :login-user (= :loading))
     (-> db :fetch-status :register-user (= :loading)))))
