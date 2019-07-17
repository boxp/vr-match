(ns vr-match.lib.component
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [secretary.core :as secretary]
            [vr-match.events]
            [vr-match.subs]
            [vr-match.route :as route]
            [vr-match.lib.components.header :refer [header]]
            [vr-match.lib.components.material-ui :as mui]))

;; (defn- header
;;   [{:keys [title]}]
;;   [:div {:style {:width "100%"
;;                  :height "56px"
;;                  :display "flex"
;;                  :justify-content "space-between"
;;                  :align-items "center"
;;                  :position "fixed"
;;                  :padding "0 16px 0 16px"
;;                  :background-color "#00adb5"
;;                  :box-sizing "border-box"
;;                  :box-shadow "2px 2px 2px 2px rgba(0, 0, 0, 0.3)"}}
;;    [:span {:style {:color "white"
;;                    :font-size "20px"
;;                    :font-weight "bold"}}
;;     title]])

(defn- loading []
  [:span "loading..."])

(defn navigation-bar-layout
  [{:keys [title] :as props} children]
  [:div {:style {:position "relative"
                 :height "100%"}}
   [header {:title title}]
   [:div {:style {:padding-top "56px"
                  :box-sizing "border-box"
                  :height "100%"
                  :width "100%"}}
    children]])

(defn app []
  (let [router (re-frame/subscribe [::vr-match.subs/router])]
    [(or (some-> @router :key route/route-table :container .call) loading)
     (-> @router :params)]))
