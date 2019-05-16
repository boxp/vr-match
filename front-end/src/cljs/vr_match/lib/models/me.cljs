(ns vr-match.lib.models.me
  (:require
   [vr-match.lib.models.plat-form :as plat-form]
   [cljs.spec.alpha :as s]))

(s/def ::id int?)
(s/def ::userName string?)
(s/def ::image (s/coll-of string?))
(s/def ::platForms (s/coll-of ::plat-form/platForm))
(s/def ::me (s/keys :req-un [::id ::userName ::image ::platForms]))

