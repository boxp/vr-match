(ns vr-match-back-end.domain.entity.platform
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.spec :as t-spec]))

(s/def ::id number?)
(s/def ::name string?)
(s/def ::url string?)
(s/def ::platform-user-id string?)

(s/def ::platform
  (s/keys :req-un [::id
                   ::name]
          :opt-un [::url
                   ::platform-user-id]))

(s/def ::example-platform-user-id ::platform-user-id)

(s/def ::platform-master
  (s/keys :req-un [::id
                   ::name
                   ::example-platform-user-id]))
