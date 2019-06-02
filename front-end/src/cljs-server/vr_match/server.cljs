(ns vr-match.server
  (:require
    [cljs.reader :as reader]
    [cljs.loader :as loader]
    [re-frame.core :as re-frame]
    [re-frame.db :as db]
    [reagent.core :refer [adapt-react-class]]
    [reagent.dom.server :as r]
    [secretary.core :as secretary]
    [vr-match.example.container]
    [vr-match.welcome.container]
    [vr-match.approach.container]
    [vr-match.profile.container]
    [vr-match.auth.containers.register]
    [vr-match.auth.containers.email-register]
    [vr-match.auth.containers.email-register-complete]
    [vr-match.wizard.container]
    [vr-match.favorite.container]
    [vr-match.myprofile.container]
    [vr-match.mypage.container]
    [vr-match.lib.component :as component]
    [vr-match.lib.components.material-ui :as mui]
    [vr-match.config :as config]
    [vr-match.events :as events]
    [vr-match.route]))

(def express (js/require "express"))
(def compression (js/require "compression"))
(def ^:export app (express))

(def api-endpoint (or js/process.env.API_ENDPOINT "http://localhost:8080"))

(def JssProvider (-> (js/require "react-jss/lib/JssProvider") .-default adapt-react-class))
(def jss (js/require "react-jss/lib/jss"))
(def sheets-registry (.-SheetsRegistry jss))

(goog-define static-file-path "/")
(goog-define dev? false)

(defn dev-setup []
  (when dev?
    (enable-console-print!)
    (println "dev mode")))

(defn app-component
  [registry generate-class-name theme sheets-manager]
  [JssProvider {:registry registry
                :generateClassName generate-class-name}
   [mui/MuiThemeProvider {:theme theme
                          :sheetsManager sheets-manager}
    [component/app]]])

(defn index
  [app-html css]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width,initial-scale=1,user-scalable=no"}]
    (when-not dev?
      [:link {:rel "manifest" :href "/manifest.json"}])
    [:link {:rel "stylesheet"
            :href "https://fonts.googleapis.com/icon?family=Material+Icons"}]
    [:style "
      body {
        font-family: -apple-system, BlinkMacSystemFont, Helvetica Neue, YuGothic, ヒラギノ角ゴ ProN W3, Hiragino Kaku Gothic ProN, Arial, メイリオ, Meiryo, sans-serif;
      }

      /* http://meyerweb.com/eric/tools/css/reset/
         v2.0 | 20110126
       License: none (public domain)
       */

      html, body, div, span, applet, object, iframe,
      h1, h2, h3, h4, h5, h6, p, blockquote, pre,
      a, abbr, acronym, address, big, cite, code,
      del, dfn, em, img, ins, kbd, q, s, samp,
      small, strike, strong, sub, sup, tt, var,
      b, u, i, center,
      dl, dt, dd, ol, ul, li,
      fieldset, form, label, legend,
      table, caption, tbody, tfoot, thead, tr, th, td,
      article, aside, canvas, details, embed,
      figure, figcaption, footer, header, hgroup,
      menu, nav, output, ruby, section, summary,
      time, mark, audio, video {
        margin: 0;
        padding: 0;
        border: 0;
        font-size: 100%;
        font: inherit;
        vertical-align: baseline;
      }
      /* HTML5 display-role reset for older browsers */
      article, aside, details, figcaption, figure,
      footer, header, hgroup, menu, nav, section {
        display: block;
      }
      body {
        line-height: 1;
      }
      ol, ul {
        list-style: none;
      }
      blockquote, q {
        quotes: none;
      }
      blockquote:before, blockquote:after,
      q:before, q:after {
        content: '';
        content: none;
      }
      table {
        border-collapse: collapse;
        border-spacing: 0;
      }
     "]
    [:style {:id "jss-server-side"} css]]
   [:body
    [:div#app
     {:dangerouslySetInnerHTML
      {:__html app-html}}]
    ]
   [:div
    {:dangerouslySetInnerHTML
     {:__html  (str "<script>window.preload = '" (-> @db/app-db pr-str) "'</script>")}}]
   [:script {:src "/static/js/compiled/cljs_base.js"}]
   [:script {:src "/static/js/compiled/app.js"}]
   (when-not dev?
     [:div
      {:dangerouslySetInnerHTML
       {:__html "
        <script>
        // Check that service workers are registered
        if ('serviceWorker' in navigator) {
            // Use the window load event to keep the page load performant
            window.addEventListener('load', () => {
                  navigator.serviceWorker.register('/sw.js');
            });
        }
        </script>
        "}}])])

(defn- render-index
  [sheets-registry generate-class-name theme sheets-manager]
  (let [app-html (r/render-to-string [app-component sheets-registry generate-class-name theme sheets-manager])]
    (r/render-to-string [index app-html (.toString sheets-registry)])))

(defn handle-render
  [req res]
  (let [request-path (.-baseUrl req)
        sheets-registry (new sheets-registry)
        sheets-manager (new js/Map)
        generate-class-name (mui/create-generate-class-name)
        theme (mui/theme)]
    (re-frame/dispatch-sync [::events/initialize {:api-endpoint api-endpoint}])
    (secretary/dispatch! request-path)
    (.format res #js {"text/html" #(.send res (render-index sheets-registry
                                                            generate-class-name
                                                            theme
                                                            sheets-manager))})))

(defn serve
  [port]
  (.listen app port))

(defn -main
  [& args]
  (let [port (-> args first js/parseInt)]
    (dev-setup)
    (serve port)))

(doto app
  (.use (compression))

  (.use "/sw.js" (.static express (str static-file-path "sw.js")))
  (.use "/manifest.json" (.static express (str static-file-path "manifest.json")))
  (.use "/favicon.ico" (.static express (str static-file-path "favicon.ico")))
  (.use "static" (.static express static-file-path))
  (.use "/static" (.static express static-file-path))
  (.use "/*" handle-render))

(set! *main-cli-fn* -main)
