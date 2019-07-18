(ns vr-match.wizard.components.wizard-nickname-step
  (:require
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.wizard.components.wizard-step :refer [wizard-step]]))

(defn- nickname-form
  [{:keys [isDuplicatedNickname
           nickname
           handleChangeInput] :as props}]
  [mui/form-control {:fullWidth true}
   [mui/text-field {:id "nickname"
                    :label "ニックネーム"
                    :defaultValue nickname
                    :onChange handleChangeInput
                    :margin "normal"}]])

(defn wizard-nickname-step
  [{:keys [me
           handleClickNext]}]
  (let [draft-nickname (r/atom (or (:name me) ""))
        handle-change-input (fn [e]
                              (reset! draft-nickname (.. e -target -value)))
        handle-click-next (fn [] (handleClickNext @draft-nickname))]
    (r/create-class
     {:display-name "wizard-nickname-step"
      :component-did-update
      (fn [this [_ old-props]]
        (let [{:keys [me]} (r/props this)
              nickname (:name me)
              old-nickname (-> old-props :me :name)]
          (when (and (= old-nickname "")
                     (seq nickname))
            (reset! draft-nickname nickname))))
      :reagent-render
      (fn []
        [wizard-step {:title [:<>
                              "ニックネームを"
                              [:br]
                              "教えてください"]
                      :form [nickname-form {:isDuplicatedNickName false
                                            :nickname @draft-nickname
                                            :handleChangeInput handle-change-input}]
                      :me me
                      :isNextDisabled (= @draft-nickname "")
                      :handleClickNext handle-click-next}])})))
