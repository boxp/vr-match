(ns vr-match-back-end.infra.repository.image
  (:import
   (com.google.cloud.storage BlobInfo Acl Acl$User Acl$Role BlobInfo Storage$BlobTargetOption)
   (java.util ArrayList))
  (:require
   [clojure.spec.alpha :as s]
   [clojure.data.codec.base64 :as b64]
   [com.stuartsierra.component :as component]
   [digest]
   [pantomime.mime :refer [mime-type-of]]
   [vr-match-back-end.infra.datasource.cloud-storage :as cloud-storage]
   [vr-match-back-end.domain.entity.image :as eimage]))

(def image-mime-type-white-list #{"image/jpeg" "image/png" "image/gif"})

(s/fdef upload-image
  :args (s/cat :c (s/keys :req-un [::cloud-storage/cloud-storage-datasource])
               :base64-string string?)
  :ret ::eimage/url)
(defn- upload-image
  [{:keys [cloud-storage-datasource] :as c}
   base64-string]
  (let [bytes (-> base64-string
                  .getBytes
                  b64/decode)
        mime-type (mime-type-of bytes)
        filename (digest/md5 bytes)]
    (if (image-mime-type-white-list mime-type)
      (-> (:service cloud-storage-datasource)
          (.create
           (.. (BlobInfo/newBuilder (:bucket-name cloud-storage-datasource) filename)
            (setAcl (ArrayList. [(Acl/of (Acl$User/ofAllUsers) Acl$Role/READER)]))
            (setContentType mime-type)
            build)
           bytes
           (make-array Storage$BlobTargetOption 0))
          .getMediaLink)
      (throw (ex-info "サポートされていない画像のファイル形式です"
                      {:image-type mime-type})))))

(defrecord ImageRepository [cloud-storage-datasource]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))
