(ns vr-match-back-end.infra.repository.converter.wizard
  (:require
   [clojure.spec.alpha :as s]
   [clojure.set :as set]
   [vr-match-back-end.domain.entity.wizard :as ewizard]))

(s/def :wizard-record/wizard_step number?)
(s/def :wizard-record/wizard_complete #{1 2})
(s/def ::wizard-record
  (s/keys :req-un [:wizard-record/wizard_step
                   :wizard-record/wizard_complete]))

(def wizard-step-wizard_step-table
  {:nickname 1
   :platforms 2
   :main-image 3})

(s/fdef wizard_step->wizard-step
  :args (s/cat :wizard_step :wizard-record/wizard_step)
  :ret ::ewizard/wizard-step)
(defn wizard_step->wizard-step
  [wizard_step]
  (or (get (set/map-invert wizard-step-wizard_step-table) wizard_step)
      :nickname))

(s/fdef wizard-step->wizard_step
  :args (s/cat :wizard-step ::ewizard/wizard-step)
  :ret :wizard-record/wizard_step)
(defn wizard-step->wizard_step
  [wizard_step]
  (or (get wizard-step-wizard_step-table wizard_step)
      1))

(s/fdef wizard_complete->wizard-complete?
  :args (s/cat :wizard_complete :wizard-record/wizard_complete)
  :ret ::ewizard/wizard-complete?)
(defn wizard_complete->wizard-complete?
  [wizard_complete]
  (= wizard_complete 1))

(s/fdef wizard-complete?->wizard_complete
  :args (s/cat :wizard-complete? ::ewizard/wizard-complete?)
  :ret :wizard-record/wizard_complete)
(defn wizard-complete?->wizard_complete
  [wizard-complete?]
  (if wizard-complete?
    1
    2))

(s/fdef record->wizard
  :args (s/cat :record ::wizard-record)
  :ret ::ewizard/wizard)
(defn record->wizard
  [record]
  (-> record
      (update :wizard_step wizard_step->wizard-step)
      (update :wizard_complete wizard_complete->wizard-complete?)
      (set/rename-keys {:wizard_step :wizard-step
                        :wizard_complete :wizard-complete})))
