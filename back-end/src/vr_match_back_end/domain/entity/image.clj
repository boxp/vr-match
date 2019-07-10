(ns vr-match-back-end.domain.entity.image
  (:require [clojure.spec.alpha :as s]))

(s/def ::url string?)
(s/def ::type #{:main :sub})

(s/def ::image
  (s/keys :req-un [::url ::type]))
