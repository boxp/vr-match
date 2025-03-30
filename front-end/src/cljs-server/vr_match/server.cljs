(ns vr-match.server
  (:require
   [cljs.reader :as reader]
   [reagent.core :as reagent]
   [reagent.dom.server :as r]
   [secretary.core :as secretary]
   ["express" :as express]
   ["compression" :as compression]
   ["react" :as react]
   ["react-dom/server" :as react-dom-server]
   ["@material-ui/core/styles" :as styles]
   ["react-jss" :refer [JssProvider SheetsRegistry]]
   [vr-match.lib.component :as component]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.events :as events]
   [vr-match.config :as config]
   ;; 各種コンテナコンポーネントのインポート
   [vr-match.example.container]
   [vr-match.welcome.container]
   [vr-match.approach.container]
   [vr-match.profile.container]
   [vr-match.auth.containers.register]
   [vr-match.auth.containers.email-register]
   [vr-match.auth.containers.email-register-complete]
   [vr-match.auth.containers.email-login]
   [vr-match.auth.containers.email-login-complete]
   [vr-match.auth.containers.twitter-login]
   [vr-match.wizard.container]
   [vr-match.favorite.container]
   [vr-match.favorited-from-users.container]
   [vr-match.matching.container]
   [vr-match.myprofile.container]
   [vr-match.mypage.container]
   [vr-match.setting.containers.top]
   [vr-match.setting.containers.third-party-authorization]
   [re-frame.core :as re-frame]
   [re-frame.db :as db]))

(def express-app (express))
(def api-endpoint (or js/process.env.API_ENDPOINT "http://localhost:8080"))

(def firebase-config
  {:apiKey js/process.env.FIREBASE_API_KEY
   :authDomain js/process.env.FIREBASE_AUTH_DOMAIN
   :databaseURL js/process.env.FIREBASE_DATABASE_URL
   :projectId js/process.env.FIREBASE_PROJECT_ID
   :storageBucket js/process.env.FIREBASE_STORAGE_BUCKET
   :messagingSenderId js/process.env.FIREBASE_MESSAGING_SENDER_ID
   :appId js/process.env.FIREBASE_APP_ID})

(def google-analytics-tracking-id js/process.env.GOOGLE_ANALYTICS_TRACKING_ID)

;; goog-define dev? は一旦残す
(goog-define dev? false)

(defn dev-setup []
  (when dev?
    (enable-console-print!)
    (println "dev mode")))

(defn render-app-html [request-path]
  (let [sheets (new (.-ServerStyleSheets styles))
        theme (mui/theme)
        generate-class-name (mui/create-generate-class-name)
        html (.renderToString react-dom-server
                (.collect sheets
                  (react/createElement (.-MuiThemeProvider styles)
                    #js{:theme theme}
                    (reagent/as-element [component/app]))))
        css (.toString sheets)]
    {:html html :css css}))

(defn index [html css]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width,initial-scale=1,user-scalable=no"}]
    (when-not dev?
      [:link {:rel "manifest" :href "/manifest.json"}])
    (when-not dev?
      [:link {:rel "apple-touch-icon" :href "/static/img/logo.png"}])
    [:title "Hito Hub"]
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
      {:__html html}}]]
   [:div
    {:dangerouslySetInnerHTML
     {:__html (str "<script>window.preload = '" (-> @db/app-db pr-str) "'</script>")}}]
   [:div
    {:dangerouslySetInnerHTML
     {:__html (str "<script>window.firebaseConfig = '" (-> firebase-config pr-str) "'</script>")}}]
   [:script {:src "/static/js/compiled/cljs_base.js"}]
   [:script {:src "/static/js/compiled/app.js"}]
   [:link {:rel "stylesheet"
           :href "https://fonts.googleapis.com/icon?family=Material+Icons"}]
   (when google-analytics-tracking-id
     [:div
      {:dangerouslySetInnerHTML
       {:__html
        (str "
        <!-- Google Analytics -->
        <script>
          window.ga=window.ga||function(){(ga.q=ga.q||[]).push(arguments)};ga.l=+new Date;
          ga('create', '" google-analytics-tracking-id "', 'auto');
          ga('send', 'pageview');
        </script>
        <script async src='https://www.google-analytics.com/analytics.js'></script>
        <!-- End Google Analytics -->
        ")}}])
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

(defn handle-render [req res]
  (let [request-path (.-baseUrl req)
        _ (re-frame/dispatch-sync [::events/initialize {:api-endpoint api-endpoint
                                                       :preload nil
                                                       :history nil}])
        _ (secretary/dispatch! request-path)
        {:keys [html css]} (render-app-html request-path)]
    (.format res #js{"text/html" #(.send res (r/render-to-string [index html css]))})))

(defn serve [port]
  (.listen express-app port))

(defn ^:export main [& args]
  (let [port (-> args first js/parseInt)]
    (dev-setup)
    (serve port)))

(doto express-app
  (.use (compression))

  ;; 静的ファイル配信を resources/public に一本化
  (.use "/static" (.static express "resources/public"))

  ;; ルートパスの特定ファイルに対する設定 (resources/public を参照)
  (.get "/sw.js" (fn [req res] (.sendFile res "sw.js" #js{:root "resources/public/"})))
  (.get "/manifest.json" (fn [req res] (.sendFile res "manifest.json" #js{:root "resources/public/"})))
  (.get "/favicon.ico" (fn [req res] (.sendFile res "favicon.ico" #js{:root "resources/public/"})))

  ;; その他のすべてのパスに対するSSRハンドラ
  (.use "/*" handle-render))

(set! *main-cli-fn* main)
