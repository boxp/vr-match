(ns vr-match.mypage.components.edit-user-name
  (:require
   [cljs.spec.alpha :as s]
   [reagent.core :as r]
   [vr-match.lib.components.material-ui :as mui]))

(s/def ::userName string?)
(s/def ::handleClickSubmit (s/fspec :args (s/cat :userName string?)
                                    :ret nil?))
(s/def ::handleCancel (s/fspec :args (s/cat)
                               :ret nil?))
(s/fdef edit-user-name
  :args (s/cat :props (s/keys :req [::userName ::handleClickSubmit ::handleCancel]))
  :ret vector?)
(defn edit-user-name
  [{:keys [userName
           handleSubmit
           handleCancel]
    :as props}]
  (let [draft-user-name (r/atom userName)]
    (fn []
      [:div {:style {:width "100%"
                     :height "100%"}}
       [mui/app-bar {:position "static"
                     :color "default"}
        [mui/tool-bar
         [mui/icon-button {:on-click handleCancel}
          [mui/icon "navigate_before"]]]]])))



