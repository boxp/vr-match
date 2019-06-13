(ns vr-match.lib.models.platform
  (:require [cljs.spec.alpha :as s]))

(s/def ::id int?)
(s/def ::name string?)
(s/def ::userId string?)
(s/def ::link string?)
(s/def ::platform (s/keys :req-un [::id ::name]
                          :opt-un [::userId ::link]))
