(ns vr-match-back-end.domain.usecase.platform
  (:require
   [integrant.core :as ig]
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [vr-match-back-end.domain.entity.platform :as eplatform]
   [vr-match-back-end.infra.repository.platform :as rplatform]))

(s/def ::platform-usecase
  (s/keys :req-un [::rplatform/platform-repository]))

(s/fdef get-platform-masters
  :args (s/cat :c ::platform-usecase)
  :ret (s/coll-of ::eplatform/platform-master))
(defn get-platform-masters
  [{:keys [platform-repository]}]
  (rplatform/get-platform-masters platform-repository))

(defmethod ig/init-key ::platform-usecase [_ u] u)

(defmethod ig/halt-key! ::platform-usecase [_ _] nil)

(defmethod ig/assert-key ::platform-usecase [_ {:keys [platform-repository]}]
  (assert platform-repository "platform-repository is required"))

