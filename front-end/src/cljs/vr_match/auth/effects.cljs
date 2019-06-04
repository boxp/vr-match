(ns vr-match.auth.effects
  (:require [firebase.app]
            [firebase.auth]
            [re-frame.core :as re-frame]))

(defonce firebase-instance (atom nil))

(re-frame/reg-fx
 ::initialize-firebase
 (fn []
   (when-not @firebase-instance
     (->> (js/firebase.initializeApp #js {"apiKey" "AIzaSyB43RXz3nt_ihJxRuElcCdUW7QOACsP-xc"
                                          "authDomain" "vr-match.firebaseapp.com"
                                          "databaseURL" "https://vr-match.firebaseio.com"
                                          "projectId" "vr-match"
                                          "storageBucket" "vr-match.appspot.com"
                                          "messagingSenderId" "431230778247"
                                          "appId" "1:431230778247:web:d195b37d884b0cc7"})
          (reset! firebase-instance)))))

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
              (re-frame/dispatch (conj callback-success email))))
           (catch
            (fn [error]
              (re-frame/dispatch (conj callback-error error)))))))))
