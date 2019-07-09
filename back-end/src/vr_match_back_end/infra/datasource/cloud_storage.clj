(ns vr-match-back-end.infra.datasource.cloud-storage
  (:import
   (com.google.cloud.storage Storage StorageOptions))
  (:require
   [clojure.spec.alpha :as s]
   [clojure.java.io :as io]
   [com.stuartsierra.component :as component]))

(s/def ::service #(instance? Storage %))
(s/def ::cloud-storage-datasource
  (s/keys :opt-un [::service]))

(s/fdef get-default-service
  :args (s/cat)
  :ret ::service)
(defn get-default-service []
  (.. (StorageOptions/getDefaultInstance)
      getService))

(defrecord CloudStorageDatasource [service]
  component/Lifecycle
  (start [this]
    (-> this
        (assoc :service (get-default-service))))
  (stop [this]
    (-> this
        (dissoc :service))))
