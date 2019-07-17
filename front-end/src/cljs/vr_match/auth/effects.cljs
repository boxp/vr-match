(ns vr-match.auth.effects
  (:require [firebase.app]
            [firebase.auth]
            [cljs.reader :refer [read-string]]
            [ajax.core :refer [ajax-request json-request-format json-response-format]]
            [re-frame.core :as re-frame]))

(defonce firebase-instance (atom nil))

(re-frame/reg-fx
 ::initialize-firebase
 (fn [config]
   (when-not @firebase-instance
     (->> (js/firebase.initializeApp config)
          (reset! firebase-instance)))))

(re-frame/reg-fx
 ::ajax
 (fn [[params]]
   (some-> params
           (assoc :format (json-request-format))
           (assoc :response-format (json-response-format {:keywords? true}))
           (assoc :handler #(re-frame/dispatch
                                [(if (first %)
                                   (:success-handler params)
                                   (:error-handler params))
                                 (some-> %
                                         second
                                         .-data
                                         read-string)]))
           ajax-request)))

(re-frame/reg-fx
 ::send-sign-in-link-to-email
 (fn [{:keys [email redirect-path callback-success callback-error] :as params}]
   (.. @firebase-instance
       auth
       (sendSignInLinkToEmail email
                              #js {"url" (str (.. js/location -origin) redirect-path)
                                   "handleCodeInApp" true})
       (then
        (fn []
          (re-frame/dispatch (conj callback-success email))))
       (catch
        (fn [error]
          (re-frame/dispatch (conj callback-error error)))))))

(re-frame/reg-fx
 ::sign-in-with-email-link
 (fn [{:keys [email callback-success callback-error]}]
   (let [location (.. js/window -location -href)]
     (when (.. @firebase-instance auth (isSignInWithEmailLink location))
       (.. @firebase-instance
           auth
           (signInWithEmailLink email location)
           (then
            (fn [result]
              (re-frame/dispatch (conj callback-success email (.. result -additionalUserInfo -isNewUser)))))
           (catch
            (fn [error]
              (re-frame/dispatch (conj callback-error error)))))))))

(re-frame/reg-fx
 ::renew-id-token
 (fn [{:keys [callback-success callback-error]}]
   (.. @firebase-instance
       auth
       -currentUser
       (getIdToken true)
       (then
        (fn [id-token]
          (re-frame/dispatch (conj callback-success id-token))))
       (catch
        (fn [error]
          (re-frame/dispatch (conj callback-error error)))))))
