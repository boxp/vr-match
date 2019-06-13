(ns vr-match.lib.models.me
  (:require
   [vr-match.lib.models.platform :as platform]
   [cljs.spec.alpha :as s]))

(s/def ::id int?)
(s/def ::userName string?)
(s/def ::image (s/coll-of string?))
(s/def ::platForms (s/coll-of ::platform/platform))
(s/def ::me (s/keys :req-un [::id ::userName ::image ::platforms]))

