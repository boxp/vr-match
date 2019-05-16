(ns vr-match.lib.models.plat-form
  (:require [cljs.spec.alpha :as s]))

(s/def ::id int?)
(s/def ::name string?)
(s/def ::platForm (s/keys :req-un [::id ::name]))
