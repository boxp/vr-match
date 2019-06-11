(ns vr-match-back-end.domain.entity.user
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.spec :as t-spec]))

(s/def ::id number?)
(s/def ::firebase_id string?)
(s/def ::name string?)
(s/def ::introduction string?)
(s/def ::created_at ::t-spec/date-time)
(s/def ::updated_at ::t-spec/date-time)

(s/def ::user
  (s/keys :req [::id ::firebase_id ::name ::introduction]
          :opt [::created_at ::updated_at]))
