(ns vr-match.mypage.components.edit-platform-dialog
  (:require
   [cljs.spec.alpha :as s]
   [reagent.core :as r]
   [vr-match.lib.models.platform :as platform-model]
   [vr-match.mypage.components.platform-expansion-panel :as platform-expansion-panel]
   ["@material-ui/core" :as material-ui]))

(s/def ::platforms (s/coll-of ::platform-model/platForm))

(defn- handle-change-platform
  [draft-platforms
   draft-platform-idx
   platformOptions
   e]
  (let [selected-id (-> e .-target .-value (js/parseInt 10))
        selected-name (->> platformOptions
                           (filter #(= selected-id (:id %)))
                           (map :name)
                           first)]
    (swap! draft-platforms
           (fn [platforms]
             (map-indexed #(if (= draft-platform-idx %1)
                             (-> %2
                                 (assoc :id selected-id)
                                 (assoc :name selected-name))
                             %2)
                          platforms)))))

(defn- handle-delete-draft-platform
  [draft-platforms
   platform-id]
  (swap! draft-platforms
         (fn [platforms]
           (remove #(= platform-id (:id %)) platforms))))

(s/fdef remove-duplicated-platform-options
  :args (s/cat :selected-platforms ::platforms
               :current-platform ::platform-model/platForm
               :platform-options ::platform-expansion-panel/platformOptions)
  :ret ::platform-expansion-panel/platformOptions)
(defn- remove-duplicated-platform-options
  [selected-platforms
   current-platform
   platform-options]
  (let [selected-ids (-> (map :id selected-platforms) set)
        current-id (:id current-platform)]
    (->> platform-options
         (remove #(and (selected-ids (:id %))
                       (not= current-id (:id %))))
         vec)))

(defn- active-add-platform-button?
  [selected-platforms platform-options]
  (< (count selected-platforms)
     (count platform-options)))

(defn- handle-click-add-platform-button
  [draft-platforms platform-options]
  (let [selected-platform-ids (->> @draft-platforms (map :id) set)
        new-platform-option (->> platform-options
                                 (filter #(not (selected-platform-ids (:id %))))
                                 first)]
    (swap! draft-platforms
           #(-> %
                vec
                (conj {:name (:name new-platform-option)
                       :id (:id new-platform-option)})))))

(defn- handle-change-platform-user-id
  [draft-platforms
   draft-platform-idx
   event]
  (let [value (.. event -target -value)]
    (swap! draft-platforms
           (fn [platforms]
             (map-indexed (fn [idx platform]
                            (if (= idx draft-platform-idx)
                              (assoc platform :platformUserId value)
                              platform))
                          platforms)))))

(s/def ::isOpen boolean?)
(s/def ::handleClickSubmit fn?)
(s/def ::handleCancel fn?)
(s/fdef edit-platform-dialog
  :args (s/cat :props (s/keys :req-un [::isOpen
                                       ::platforms
                                       ::platform-expansion-panel/platformOptions
                                       ::handleClickSubmit
                                       ::handleCancel]))
  :ret vector?)
(defn edit-platform-dialog
  [props]
  (let [draft-platforms (r/atom (-> props :platforms))]
    (r/create-class
     {:display-name "edit-platform-dialog"
      :component-did-update
      (fn [this [_ old-props]]
        (let [{:keys [platforms]} (r/props this)
              old-platforms (:platforms old-props)]
          (when (and (nil? old-platforms)
                     (seq platforms))
            (reset! draft-platforms platforms))))
      :reagent-render
      (fn [{:keys [isOpen
                   platforms
                   platformOptions
                   handleSubmit
                   handleCancel]}]
        [:> material-ui/Dialog {:open isOpen
                                :onClose handleCancel
                                :aria-labelledby "活動場所を編集"
                                :full-screen true}
         [:> material-ui/DialogTitle "活動場所を編集"]
         [:> material-ui/DialogContent
          [:<>
           [:ul
            (->> @draft-platforms
                 (map-indexed
                  (fn [draft-platform-idx draft-platform]
                    ^{:key draft-platform-idx}
                    [:li {:style {:margin-top (if (not= draft-platform-idx 0)
                                                "8px"
                                                "0")}}
                     [platform-expansion-panel/platform-expansion-panel
                      {:platform draft-platform
                       :platformIdx draft-platform-idx
                       :platformOptions (remove-duplicated-platform-options
                                         @draft-platforms
                                         draft-platform
                                         platformOptions)
                       :handleChangePlatform #(handle-change-platform
                                               draft-platforms
                                               draft-platform-idx
                                               platformOptions
                                               %)
                       :handleClickDelete #(handle-delete-draft-platform
                                            draft-platforms
                                            (:id draft-platform))
                       :handleChangePlatformUserId #(handle-change-platform-user-id
                                                     draft-platforms
                                                     draft-platform-idx
                                                     %)}]]))
                 doall)]
           [:div {:style {:margin-top "16px"}}
            [:> material-ui/Button
             {:color "primary"
              :variant "contained"
              :size "large"
              :full-width true
              :on-click #(handle-click-add-platform-button
                          draft-platforms
                          platformOptions)
              :disabled (-> (active-add-platform-button?
                             @draft-platforms
                             platformOptions)
                            not)}
             [:> material-ui/Icon
              "add"]]]]]
         [:> material-ui/DialogActions
          [:> material-ui/Button {:on-click (fn []
                                              (handleCancel)
                                              (reset! draft-platforms platforms))}
           "キャンセル"]
          [:> material-ui/Button {:on-click #(handleSubmit @draft-platforms)
                                  :color "primary"}
           "決定"]]])})))
