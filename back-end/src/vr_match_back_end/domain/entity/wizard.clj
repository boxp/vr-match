(ns vr-match-back-end.domain.entity.wizard
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::wizard-step #{:nickname
                       :platforms
                       :main-image})
(s/def ::wizard-complete? boolean?)

(s/def ::wizard
  (s/keys :req-un [::wizard-step ::wizard-complete?]))
