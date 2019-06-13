(ns vr-match-back-end.app.my-webapp.converter
  (:require
   [clojure.spec.alpha :as s]
   [clojure.set :as set]
   [vr-match-back-end.domain.entity.user :as euser]))

(s/fdef user->User
  :args (s/cat :user ::euser/user)
  :ret map?)
(defn user->User
  [user]
  (-> user
      (dissoc :firebase_id)
      (dissoc :session_cookie)
      (dissoc :created_at)
      (dissoc :updated_at)))
