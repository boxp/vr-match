(ns vr-match-back-end.infra.datasource.cloud-storage
  (:import
   (com.google.cloud.storage Storage StorageOptions))
  (:require
   [clojure.spec.alpha :as s]
   [clojure.java.io :as io]
   [com.stuartsierra.component :as component]
   [integrant.core :as ig]))

(s/def ::service #(instance? Storage %))
(s/def ::bucket-name string?)
(s/def ::cloud-storage-datasource
  (s/keys
   :req-un [::bucket-name ::service]))

(s/fdef get-default-service
  :args (s/cat)
  :ret ::service)
(defn get-default-service []
  (.. (StorageOptions/getDefaultInstance)
      getService))

(defmethod ig/init-key ::cloud-storage-datasource [_ {:keys [bucket-name]}]
  {:service (get-default-service)
   :bucket-name bucket-name})

(defmethod ig/halt-key! ::cloud-storage-datasource [_ m]
  (-> m
      (dissoc :service)
      (dissoc :bucket-name)))

(defmethod ig/prep-key ::cloud-storage-datasource [_ config]
  (merge config {:bucket-name ""}))

(defmethod ig/pre-init-spec ::cloud-storage-datasource [_]
  (s/keys :req-un [::bucket-name]))
