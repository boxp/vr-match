(ns vr-match.client
  (:require
   [cljs.spec.alpha :as s]
   ["react-dom" :as react-dom]
   [cljs.loader :as loader]
   [cljs.reader :as reader]
   [pushy.core :as pushy]
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [secretary.core :as secretary]
   ["@material-ui/core/styles" :refer [MuiThemeProvider createMuiTheme createGenerateClassName]]
   ["@material-ui/core/colors" :as colors]
   ["react-jss" :refer [JssProvider SheetsRegistry]]
   [vr-match.events :as events]
   [vr-match.lib.component :as component]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.route :as route]
   [vr-match.config :as config]
   [vr-match.util :as util]))

;; Material-UIテーマの設定
(def mui-theme (mui/theme))
(def mui-generate-class-name (mui/create-generate-class-name))

;; SSRで生成されたCSSを取り除く関数
(defn remove-ssr-styles []
  (let [jss-styles (.getElementById js/document "jss-server-side")]
    (when (and jss-styles (.-parentNode jss-styles))
      (.removeChild (.-parentNode jss-styles) jss-styles))))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(def history
  (pushy/pushy secretary/dispatch!
               (fn [x] (when (secretary/locate-route x)
                         (re-frame/dispatch [::events/send-pageview x])
                         x))))

(defn hook-history []
  (pushy/start! history))

(defn index []
  (reagent/create-class
   {:component-did-mount
    (fn []
      (remove-ssr-styles))
    :reagent-render
    (fn []
      [:> JssProvider {:generate-class-name mui-generate-class-name}
       [:> MuiThemeProvider {:theme mui-theme}
        [component/app]]])}))

(defn mount-root []
  (react-dom/hydrate (reagent/as-element [index])
                     (.getElementById js/document "app")))

(defn ^:export remount-for-figwheel []
  (re-frame/clear-subscription-cache!)
  (reagent/render-component [index]
                            (.getElementById js/document "app")))

(defn- preload-state []
  (some->
   js/window
   (aget "preload")
   reader/read-string))

(defn ^:export init []
  (let [preload (preload-state)]
    (util/universal-load (-> preload :router :key route/route-table :module-name)
                         (fn []
                           (re-frame/dispatch-sync
                            [::events/initialize
                             {:history history
                              :preload preload}])
                           (re-frame/dispatch
                            [::events/initialize-worker])
                           (dev-setup)
                           (hook-history)
                           (mount-root)))))

(set! (. js/window -onload) init)

(loader/set-loaded! :client)
