(ns vr-match-back-end.domain.entity.image
  (:require [clojure.spec.alpha :as s]
            [clj-time.spec :as t-spec]))

(s/def ::url string?)
(s/def ::type #{:main :sub})
(s/def ::placeholder-color string?)

(s/def ::image
  (s/keys :req-un [::url ::type]
          :opt-un [::placeholder-color]))
