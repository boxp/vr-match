(ns vr-match-back-end.domain.entity.user
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.spec :as t-spec]
   [vr-match-back-end.domain.entity.platform :as eplatform]))

(s/def ::id number?)
(s/def ::firebase_id string?)
(s/def ::name string?)
(s/def ::introduction string?)
(s/def ::session_cookie string?)
(s/def ::platforms (s/coll-of ::eplatform/platform))
(s/def ::created_at ::t-spec/date-time)
(s/def ::updated_at ::t-spec/date-time)

(s/def ::user
  (s/keys :req-un [::id
                   ::firebase_id
                   ::name
                   ::introduction
                   ::session_cookie]
          :opt-un [::platforms
                   ::created_at
                   ::updated_at]))
