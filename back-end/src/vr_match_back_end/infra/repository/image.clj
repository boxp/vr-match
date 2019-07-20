(ns vr-match-back-end.infra.repository.image
  (:import
   (com.google.cloud.storage BlobInfo Acl Acl$User Acl$Role BlobInfo Storage$BlobTargetOption)
   (java.util ArrayList))
  (:require
   [clojure.spec.alpha :as s]
   [clojure.data.codec.base64 :as b64]
   [clojure.java.jdbc :as jdbc]
   [clojure.set :as set]
   [clj-time.core :as t]
   [clj-time.coerce :refer [to-long]]
   [clj-time.spec :as t-spec]
   [com.stuartsierra.component :as component]
   [hugsql.core :refer [def-db-fns]]
   [digest]
   [pantomime.mime :refer [mime-type-of]]
   [vr-match-back-end.infra.datasource.cloud-storage :as cloud-storage]
   [vr-match-back-end.domain.entity.image :as eimage]))

(s/def ::image-repository (s/keys :req-un [::cloud-storage/cloud-storage-datasource]))

(def image-mime-type-white-list #{"image/jpeg" "image/png" "image/gif"})

(def-db-fns "vr_match_back_end/infra/repository/sql/image.sql")

(s/fdef upload-image
  :args (s/cat :c ::image-repository
               :base64-string string?)
  :ret ::eimage/url)
(defn- upload-image
  [{:keys [cloud-storage-datasource] :as c}
   base64-string]
  (let [bytes (-> (str base64-string (to-long (t/now)))
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
                      {:type :unsupported-image-type
                       :image-type mime-type})))))

(s/fdef add-image
  :args (s/cat :c ::image-repository
               :base64-string string?)
  :ret (s/keys :req-un [::eimage/id
                        ::eimage/url]))
(defn add-image
  [{:keys [mysql-datasource
           cloud-storage-datasource] :as c}
   base64-string]
  (let [image-url (upload-image c base64-string)
        [_ image-id] (insert-image (:db mysql-datasource)
                                   {:url image-url})]
    {:id image-id
     :url image-url}))

(defrecord ImageRepository [cloud-storage-datasource mysql-datasource]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))
