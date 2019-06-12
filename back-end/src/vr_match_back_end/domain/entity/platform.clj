(ns vr-match-back-end.domain.entity.platform
  (:require
   [clojure.spec.alpha :as s]
   [clj-time.spec :as t-spec]))

(s/def ::id number?)
(s/def ::name string?)
(s/def ::url string?)

(s/def ::platform
  (s/keys :req [::id
                ::name
                ::url]))
