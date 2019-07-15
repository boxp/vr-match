(ns vr-match-back-end.domain.entity.platform
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.spec :as t-spec]))


(s/def ::id number?)
(s/def ::name string?)
(s/def ::url string?)
(s/def ::platform_user_id string?)

(s/def ::platform
  (s/keys :req-un [::id
                   ::name
                   ::url]
          :opt-un [::platform_user_id]))

(s/def ::example_platform_user_id ::platform_user_id)

(s/def ::platform-master
  (s/keys :req-un [::id
                   ::name
                   ::example_platform_user_id]))
