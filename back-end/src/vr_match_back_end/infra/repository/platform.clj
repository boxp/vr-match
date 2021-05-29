(ns vr-match-back-end.infra.repository.platform
  (:require
   [clojure.spec.alpha :as s]
   [clojure.java.jdbc :as jdbc]
   [clojure.set :as set]
   [clj-time.spec :as t-spec]
   [com.stuartsierra.component :as component]
   [hugsql.core :refer [def-db-fns]]
   [vr-match-back-end.domain.entity.platform :as eplatform]
   [vr-match-back-end.infra.datasource.mysql :as mysql]
   [integrant.core :as ig]))

(s/def ::platform-repository
  (s/keys :req-un [::mysql/mysql-datasource]))

(def-db-fns "vr_match_back_end/infra/repository/sql/platform.sql")

(s/def :platform-record/id number?)
(s/def :platform-record/name string?)
(s/def :platform-record/url_template string?)
(s/def :platform-record/example_user_id string?)
(s/def ::platform-record
  (s/keys :req-un [:platform-record/id
                   :platform-record/name
                   :platform-record/url_template
                   :platform-record/example_user_id]))
(s/fdef record->platform-master
  :args (s/cat :record ::platform-record)
  :ret ::eplatform/platform-master)
(defn record->platform-master
  [record]
  (-> record
      (select-keys [:id :name :example_user_id])
      (set/rename-keys {:example_user_id :example-platform-user-id})))

(s/fdef get-platform-masters
  :args (s/cat :c ::platform-repository)
  :ret (s/coll-of ::eplatform/platform-master))
(defn get-platform-masters
  [{:keys [mysql-datasource]}]
  (->> (platform (:db mysql-datasource))
       (map record->platform-master)))

(defmethod ig/init-key ::platform-repository [_ r] r)

(defmethod ig/halt-key! ::platform-repository [_ _] nil)

(defmethod ig/pre-init-spec ::platform-repository [_]
  (s/keys :req-un [:mysql/mysql-datasource]))
