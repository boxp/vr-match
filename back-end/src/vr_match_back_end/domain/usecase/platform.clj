(ns vr-match-back-end.domain.usecase.platform
  (:require
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.domain.entity.platform :as eplatform]
   [vr-match-back-end.infra.repository.user :as ruser]
   [vr-match-back-end.infra.repository.platform :as rplatform]))

(s/def ::platform-usecase
  (s/keys :req-un [::rplatform/platform-repository]))

(s/fdef get-platform-masters
  :args (s/cat :c ::platform-usecase)
  :ret (s/coll-of ::eplatform/platform-master))
(defn get-platform-masters
  [{:keys [platform-repository]}]
  (rplatform/get-platform-masters platform-repository))

(defrecord PlatformUsecase [platform-repository]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))
