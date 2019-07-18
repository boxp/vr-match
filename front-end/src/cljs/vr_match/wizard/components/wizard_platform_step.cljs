(ns vr-match.wizard.components.wizard-platform-step
  (:require
   [cljs.spec.alpha :as s]
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.wizard.components.wizard-step :refer [wizard-step]]))

(s/def ::id number?)
(s/def ::selected? boolean?)
(s/def ::name string?)
(s/def ::platform
  (s/keys :req [::id ::selected? ::name]))
(s/def ::platforms
  (s/coll-of ::platform))

(s/fdef platform-select
  :args (s/cat :platforms ::platforms
               :id ::id)
  :ret ::platforms)
(defn- platform-select
  [platforms id]
  (->> platforms
       (map (fn [platform]
              (if (= id (:id platform))
                (update platform :selected? not)
                platform)))))

(s/def ::platform-form-props
  (s/keys :req [::platforms]))
(s/fdef platform-form
  :args (s/cat :props ::platform-form-props))
(defn- platform-form
  [{:keys [platforms handleChange] :as props}]
  [mui/form-control {:fullWidth true
                     :component "fieldset"}
   (map (fn [{:keys [name selected? id] :as platform}]
          ^{:key id}
          [mui/form-control-label {:label name
                                   :control (r/as-element [mui/checkbox {:checked selected?
                                                                         :value (str id)
                                                                         :onChange #(handleChange id)}])}])
        platforms)])

(defn platformChoices->platforms
  [platformChoices]
  (map #(assoc % :selected? false) platformChoices))

(s/def ::props
  (s/keys :req [::platforms]))
(s/fdef wizard-platform-step
  :args (s/cat :props ::props))
(defn wizard-platform-step
  [{:keys [me
           platforms
           platformChoices
           handleClickNext]}]
  (let [draft-platforms (r/atom (platformChoices->platforms platformChoices))
        handle-change (fn [id value]
                        (swap! draft-platforms #(platform-select % id)))
        handle-click-next (fn []
                            (->> @draft-platforms
                                 (filter #(:selected? %))
                                 (map #(select-keys % [:id :name]))
                                 handleClickNext))]
    (r/create-class
     {:display-name "wizard-platform-step"
      :component-did-update
      (fn [this [_ old-props]]
        (let [{:keys [platformChoices]} (r/props this)
              old-platformChoices (:platformChoices old-props)]
          (when (and (nil? old-platformChoices)
                     (seq platformChoices))
            (reset! draft-platforms
                    (platformChoices->platforms platformChoices)))))
      :reagent-render
      (fn []
        [wizard-step {:title [:<>
                              "活動中の場所を"
                              [:br]
                              "教えてください"]
                      :form [platform-form {:platforms @draft-platforms
                                            :handleChange handle-change}]
                      :me me
                      :isNextDisabled false
                      :handleClickNext handle-click-next}])})))
