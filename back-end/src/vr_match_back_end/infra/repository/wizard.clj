(ns vr-match-back-end.infra.repository.wizard
  (:require
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [hugsql.core :refer [def-db-fns]]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.domain.entity.wizard :as ewizard]
   [vr-match-back-end.infra.repository.converter.wizard :as cwizard]
   [vr-match-back-end.infra.datasource.mysql :as mysql]))

(def-db-fns "vr_match_back_end/infra/repository/sql/user_wizard.sql")

(s/def ::wizard-repository
  (s/keys :req-un [::mysql/mysql-datasource]))

(s/fdef get-wizard-by-user-id
  :args (s/cat :c ::wizard-repository
               :user-id ::euser/id)
  :ret ::ewizard/wizard)
(defn get-wizard-by-user-id
  [{:keys [mysql-datasource]}
   user-id]
  (->
   (user_wizard-by-user_id
    (:db mysql-datasource)
    {:user_id user-id})
   cwizard/record->wizard))

(defrecord WizardRepository [mysql-datasource]
  component/Lifecycle
  (start [this] this)
  (stop [this] this))
