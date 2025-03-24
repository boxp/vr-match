# VR-Match SSR改善実行計画

このドキュメントでは、VR-Matchプロジェクトのフロントエンド部分、特にサーバーサイドレンダリング（SSR）に関する問題を解決するための具体的な実行計画を記述します。

## 目的

- JavaScriptパッケージの二重管理を解消する
- ハックとワークアラウンドを取り除く
- 標準的なSSR実装パターンに移行する
- ビルドプロセスを簡素化する

## 移行計画概要

1. **準備フェーズ** - shadow-cljsの導入と環境構築
2. **シム実装フェーズ** - CLJSJSパッケージからnpmパッケージへの移行を円滑にするためのシム（互換レイヤー）の実装
3. **クライアント移行フェーズ** - クライアントコードを更新
4. **サーバー移行フェーズ** - SSRサーバーコードを更新
5. **ビルド改善フェーズ** - ビルドスクリプトとDockerfileの更新
6. **検証フェーズ** - 実装の確認とバグ修正

## フェーズ1: 準備フェーズ

### 1.1 shadow-cljsのインストールと設定

#### shadow-cljs.ednの作成

ファイルパス: `front-end/shadow-cljs.edn`

```clojure
{:source-paths ["src/clj" "src/cljs" "src/cljs-client" "src/cljs-server" "src/cljs-worker"]
 :dependencies [[reagent "0.10.0"]
                [re-frame "1.2.0"]
                [clj-commons/secretary "1.2.4"]
                [kibu/pushy "0.3.8"]
                [e85th/venia "0.2.5-1"]
                [cljs-ajax "0.8.4"]]

 ;; 注: npmパッケージのエイリアスを設定
 :npm-aliases {"material-ui" "@material-ui/core"}

 :builds
 {:client {:target :browser
           :output-dir "resources/public/js/compiled"
           :asset-path "/static/js/compiled"
           :module-loader true
           :modules {:cljs-base {:entries []
                                 :output-name "cljs_base.js"}
                     :client {:entries [vr-match.client]
                              :depends-on #{:cljs-base}
                              :output-name "app.js"}
                     :example {:entries [vr-match.example.container]
                               :depends-on #{:client}
                               :output-name "example.js"}
                     ;; 他のモジュールもproject.cljから移行
                     :worker {:entries [vr-match.worker]
                              :depends-on #{:cljs-base}
                              :web-worker true  ;; Web Workerとして扱う
                              :init-fn vr-match.worker/init
                              :output-name "worker.js"}}
           :devtools {:after-load vr-match.client/remount-for-figwheel}}
  
  :server {:target :node-script
           :output-dir "target/server/prod/js/compiled"
           :output-to "target/server/prod/js/compiled/server.js"
           :main vr-match.server/main
           :compiler-options {:optimizations :simple}}
  
  :worker {:target :browser
           :output-dir "resources/public/prod/worker/js/compiled"
           :output-to "resources/public/prod/worker/js/compiled/worker.js"
           :compiler-options {:optimizations :advanced}}}}
```

#### package.jsonの更新

ファイルパス: `front-end/package.json`

```json
{
  "name": "vr-match",
  "version": "1.0.0",
  "description": "VR Match Frontend",
  "scripts": {
    "clean": "rm -rf resources/public/js/compiled resources/public/prod/js/compiled target",
    "shadow-cljs": "shadow-cljs",
    "build": "npm run build:prod",
    "build:dev": "shadow-cljs compile client server",
    "build:prod": "shadow-cljs release client server && npm run workbox",
    "watch": "shadow-cljs watch client server",
    "workbox": "workbox generateSW",
    "start": "node target/server/prod/js/compiled/server.js"
  },
  "dependencies": {
    "compression": "1.8.0",
    "create-react-class": "15.7.0",
    "express": "4.17.1",
    "firebase": "5.7.3",
    "react": "16.14.0",
    "react-dom": "16.14.0",
    "react-jss": "8.6.1",
    "@material-ui/core": "3.9.3",
    "@material-ui/icons": "3.0.2",
    "whatwg-fetch": "3.0.0",
    "xmlhttprequest": "1.8.0"
  },
  "devDependencies": {
    "shadow-cljs": "^2.20.20",
    "workbox-cli": "4.3.1"
  }
}
```

### 1.2 CLJSJSパッケージから直接npmパッケージへの依存関係の移行

既存のCLJSJSパッケージをインストールするnpmパッケージに置き換えるマッピング：

| CLJSJSパッケージ | npmパッケージ |
|-----------------|--------------|
| cljsjs/react    | react |
| cljsjs/react-dom | react-dom |
| cljsjs/material-ui | @material-ui/core |
| cljsjs/firebase | firebase |

## フェーズ2: シム実装フェーズ

### 2.1 CLJSJSパッケージ用シムの作成

CLJSJSパッケージからnpmパッケージへの移行を簡単にするために、一時的な互換レイヤー（シム）を作成します。ただし、Material-UIについてはnpm-aliasesの設定で対応するため、シムは作成しません。

#### Reactのシム

ファイルパス: `front-end/src/cljs/cljsjs/react.cljs`

```clojure
(ns cljsjs.react
  (:require ["react" :as react]))

;; グローバル変数の公開（既存のコードとの互換性を保つため）
(js/goog.exportSymbol "React" react)
```

#### React DOMのシム

ファイルパス: `front-end/src/cljs/cljsjs/react/dom.cljs`

```clojure
(ns cljsjs.react.dom
  (:require ["react-dom" :as react-dom]
            ["react-dom/server" :as react-dom-server]))

;; グローバル変数の公開
(js/goog.exportSymbol "ReactDOM" react-dom)
(js/goog.exportSymbol "ReactDOMServer" react-dom-server)
```

#### Firebaseのシム

ファイルパス: `front-end/src/cljs/cljsjs/firebase.cljs`

```clojure
(ns cljsjs.firebase
  (:require ["firebase/app" :as firebase]))

;; グローバル変数の公開
(js/goog.exportSymbol "Firebase" firebase)

;; 追加サービスのインポート（必要に応じて）
(defn import-auth []
  (js/require "firebase/auth"))

(defn import-firestore []
  (js/require "firebase/firestore"))
```

### 2.2 Material-UIの移行戦略

Material-UIについては、npm-aliases機能を活用し、シムなしでの移行を試みます：

1. **shadow-cljs.ednでのnpm-aliasesの設定**
   ```clojure
   :npm-aliases {"material-ui" "@material-ui/core"}
   ```
   
2. **徐々に直接インポートスタイルに移行**

   既存のインポート:
   ```clojure
   (ns vr-match.lib.components.linear-progress
     (:require
      [material-ui] ;; npm-aliasesにより@material-ui/coreにマッピング
      [reagent.core :as reagent]))
   ```

   将来的に移行するスタイル:
   ```clojure
   (ns vr-match.lib.components.linear-progress
     (:require
      ["@material-ui/core/LinearProgress" :as LinearProgress]
      [reagent.core :as reagent]))
   ```

このアプローチを採用することで、シム実装の複雑さを回避し、より直接的な方法でpackage管理の問題に対処します。ただし、問題が発生した場合はシム実装を検討します。

### 2.3 Material-UIを直接参照しているコンポーネントの修正リスト

プロジェクト内で`material-ui`を直接requireしているファイルおよび`js/MaterialUI`としてグローバル変数からアクセスしているコンポーネントが複数見つかりました。これらのコンポーネントは以下の手順で修正する必要があります。

#### 修正対象ファイル

**`material-ui`をrequireしているファイル**:
1. `front-end/src/cljs/vr_match/lib/components/linear_progress.cljs` (修正済み)
2. `front-end/src/cljs/vr_match/lib/components/progress_button.cljs`
3. `front-end/src/cljs/vr_match/approach/components/empty.cljs`
4. `front-end/src/cljs/vr_match/approach/components/reset_all_skip_alert.cljs`
5. `front-end/src/cljs/vr_match/auth/components/email_login.cljs`
6. `front-end/src/cljs/vr_match/auth/components/email_register.cljs`
7. `front-end/src/cljs/vr_match/auth/components/email_login_complete.cljs`
8. `front-end/src/cljs/vr_match/auth/components/email_register_complete.cljs`
9. `front-end/src/cljs/vr_match/auth/components/twitter_login.cljs`
10. `front-end/src/cljs/vr_match/setting/components/cannot_unlink_third_party_alert.cljs`
11. `front-end/src/cljs/vr_match/setting/components/unlink_confirmation_alert.cljs`
12. `front-end/src/cljs/vr_match/favorite/component.cljs`
13. `front-end/src/cljs/vr_match/matching/component.cljs`
14. `front-end/src/cljs/vr_match/favorited_from_users/component.cljs`
15. `front-end/src/cljs/vr_match/mypage/components/platform_expansion_panel.cljs`
16. `front-end/src/cljs/vr_match/mypage/components/edit_platform_dialog.cljs`

#### 修正例

例えば、`progress_button.cljs`の修正前・修正後は以下のようになります：

**修正前:**
```clojure
(ns vr-match.lib.components.progress-button
  (:require ["material-ui"]))

(defn progress-button
  [{:keys [loading?] :as props} children]
  [:div {:style {:position "relative"}}
   [:> js/MaterialUI.Button (-> props
                                (dissoc :loading?)
                                (assoc :disabled loading?))
    children]
   (when loading?
     [:> js/MaterialUI.CircularProgress {:size 24
                                         :style {:position "absolute"
                                                 :top "50%"
                                                 :left "50%"
                                                 :margin-top "-12"
                                                 :margin-left "-12"}}])])
```

**修正後:**
```clojure
(ns vr-match.lib.components.progress-button
  (:require
   ["@material-ui/core/Button" :as Button]
   ["@material-ui/core/CircularProgress" :as CircularProgress]))

(defn progress-button
  [{:keys [loading?] :as props} children]
  [:div {:style {:position "relative"}}
   [:> Button (-> props
                  (dissoc :loading?)
                  (assoc :disabled loading?))
    children]
   (when loading?
     [:> CircularProgress {:size 24
                           :style {:position "absolute"
                                   :top "50%"
                                   :left "50%"
                                   :margin-top "-12"
                                   :margin-left "-12"}}])])
```

#### 修正方針

各コンポーネントにおいて以下の修正を行います：

1. `["material-ui"]` という一括インポートを個別コンポーネントのインポートに変更
2. `js/MaterialUI.XXX` の参照を直接インポートしたコンポーネントへの参照に変更

例えば：
- `[:> js/MaterialUI.Button ...]` → `[:> Button ...]`
- `[:> js/MaterialUI.Dialog ...]` → `[:> Dialog ...]`

#### 修正スケジュール

フェーズ2のシム実装フェーズ終了後、フェーズ3のクライアント移行フェーズで段階的に修正していきます。重要度の高いコンポーネントから順に修正し、各コンポーネントの修正後にテストを行い、問題がないことを確認します。

### 2.4 Firebaseの参照方法の修正リスト

コードベース内でFirebaseを参照している箇所も同様に修正が必要です。特に、以下のようなインポート形式を修正する必要があります。

#### 修正対象ファイル

1. `front-end/src/cljs/vr_match/auth/effects.cljs`
2. その他Firebaseを参照しているファイル

#### 修正例

**修正前:**
```clojure
(ns vr-match.auth.effects
  (:require [firebase.app]
            [firebase.auth]
            [cljs.reader :refer [read-string]]
            [ajax.core :refer [ajax-request json-request-format json-response-format]]
            [re-frame.core :as re-frame]))
```

**修正後:**
```clojure
(ns vr-match.auth.effects
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            [cljs.reader :refer [read-string]]
            [ajax.core :refer [ajax-request json-request-format json-response-format]]
            [re-frame.core :as re-frame]))
```

#### 修正方針

Firebaseの参照方法は以下のパターンに統一します：

1. **メインパッケージの参照**:
   - `["firebase/app" :as firebase]`をrequireする
   - インポートした`firebase`変数を通してFirebaseの基本機能にアクセスする

2. **追加サービスの参照**:
   - 必要な追加サービスを`["firebase/auth"]`などとして直接requireする
   - 追加サービスはrequireするだけで自動的にfirebaseオブジェクトに機能が追加される

3. **グローバル変数を避ける**:
   - `js/firebase`などのグローバル変数への参照を避け、インポートした`firebase`変数を使用する

この方法は、依存関係が明示的になるため、コードの可読性と保守性が向上します。また、shadow-cljsの最適化の恩恵も受けやすくなります。

### 2.5 JSパッケージのインポート方法に関する一般的な修正方針

JSパッケージ（Material-UI、Firebase、Reactなど）のインポート方法について、以下の原則に従って修正を行います：

1. **直接インポート**: 各コンポーネントやサービスを直接インポートする方法を採用します
   - Material-UI: `["@material-ui/core/Button" :as Button]`
   - Firebase: `["firebase/app" :as firebase]`, `["firebase/auth"]`
   - React: `["react" :as react]`, `["react-dom" :as react-dom]`

2. **名前空間の統一**: 同じパッケージを参照する場合、import方法を統一します

3. **グローバル変数の使用制限**: `js/MaterialUI`や`js/firebase`などのグローバル変数への直接アクセスを避け、インポートした変数を使用します

この修正アプローチにより、より明示的な依存関係管理が可能になり、コード品質とビルドプロセスが改善されます。

#### 修正スケジュール

フェーズ2のシム実装フェーズ終了後、フェーズ3のクライアント移行フェーズで段階的に修正していきます。重要度の高いコンポーネントから順に修正し、各コンポーネントの修正後にテストを行い、問題がないことを確認します。

## フェーズ3: クライアント移行フェーズ

### 3.1 クライアントコードの更新

#### クライアントエントリーポイントの更新

ファイルパス: `front-end/src/cljs-client/vr_match/client.cljs`

```clojure
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
```

#### Material-UIラッパーコンポーネントの更新

ファイルパス: `front-end/src/cljs/vr_match/lib/components/material_ui.cljs`

```clojure
(ns vr-match.lib.components.material-ui
  (:require
   [reagent.core :as reagent]
   ["@material-ui/core/styles" :refer [createMuiTheme createGenerateClassName]]))

(defn theme []
  (createMuiTheme
   #js{:palette
       #js{:primary
           #js{:main "#556cd6"}
           :secondary
           #js{:main "#19857b"}
           :error
           #js{:main "#f44336"}
           :background
           #js{:default "#fff"}}}))

(defn create-generate-class-name []
  (createGenerateClassName))
```

## フェーズ4: サーバー移行フェーズ

### 4.1 SSRサーバーコードの更新

#### サーバーエントリーポイントの更新

ファイルパス: `front-end/src/cljs-server/vr_match/server.cljs`

```clojure
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
   ["@material-ui/styles" :refer [MuiThemeProvider ServerStyleSheets createMuiTheme]]
   ["react-jss" :refer [JssProvider SheetsRegistry]]
   [vr-match.lib.component :as component]
   [vr-match.lib.components.material-ui :as mui]
   [vr-match.events :as events]
   [vr-match.config :as config]
   ;; 各種コンテナコンポーネントのインポート
   [vr-match.example.container]
   [vr-match.welcome.container]
   ;; 他のコンテナコンポーネント...
   ))

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

(goog-define static-file-path "/")
(goog-define dev? false)

(defn dev-setup []
  (when dev?
    (enable-console-print!)
    (println "dev mode")))

(defn render-app-html [request-path]
  (let [sheets (new ServerStyleSheets)
        theme (mui/theme)
        generate-class-name (mui/create-generate-class-name)
        html (.renderToString react-dom-server
                (.collect sheets
                  (react/createElement MuiThemeProvider
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
    ;; リセットCSSなど
    [:style "/* リセットCSS */"]
    [:style {:id "jss-server-side"} css]]
   [:body
    [:div#app
     {:dangerouslySetInnerHTML
      {:__html html}}]]
   [:div
    {:dangerouslySetInnerHTML
     {:__html (str "<script>window.preload = '" (-> @re-frame.db/app-db pr-str) "'</script>")}}]
   [:div
    {:dangerouslySetInnerHTML
     {:__html (str "<script>window.firebaseConfig = '" (-> firebase-config pr-str) "'</script>")}}]
   [:script {:src "/static/js/compiled/cljs_base.js"}]
   [:script {:src "/static/js/compiled/app.js"}]
   [:link {:rel "stylesheet"
           :href "https://fonts.googleapis.com/icon?family=Material+Icons"}]
   ;; Google Analyticsスクリプト
   (when google-analytics-tracking-id
     [:div
      {:dangerouslySetInnerHTML
       {:__html "/* Google Analyticsスクリプト */"}}])
   ;; Service Workerスクリプト
   (when-not dev?
     [:div
      {:dangerouslySetInnerHTML
       {:__html "/* Service Workerスクリプト */"}}])])

(defn handle-render [req res]
  (let [request-path (.-baseUrl req)
        {:keys [html css]} (render-app-html request-path)]
    (.format res #js{"text/html" #(.send res (r/render-to-string [index html css]))})))

(defn serve [port]
  (.listen express-app port))

(defn -main [& args]
  (let [port (-> args first js/parseInt)]
    (dev-setup)
    (serve port)))

(doto express-app
  (.use (compression))
  (.use "/sw.js" (.static express (str static-file-path "sw.js")))
  (.use "/manifest.json" (.static express (str static-file-path "manifest.json")))
  (.use "/favicon.ico" (.static express (str static-file-path "favicon.ico")))
  (.use "static" (.static express static-file-path))
  (.use "/static" (.static express static-file-path))
  (.use "/*" handle-render))

(set! *main-cli-fn* -main)
```

### 4.2 プリアンブルファイルの簡素化

ファイルパス: `front-end/resources/ssr-preamble.js`

```javascript
// 簡素化したプリアンブルファイル
// shadow-cljsを使用することでほとんど不要になりますが、
// 移行期間中は一部の機能をサポートする場合があります

// Material-UIのSSRサポートのためのグローバル設定
global.window = global;
global.window.navigator = {
  userAgent: ""
};
global.window.localStorage = null;
```

## フェーズ5: ビルド改善フェーズ

### 5.1 Dockerfileの更新

ファイルパス: `front-end/Dockerfile`

```dockerfile
# ビルドステージ
FROM node:8.17-alpine as build

# 作業ディレクトリの作成
WORKDIR /usr/src/app

# パッケージ依存関係のインストール
COPY package.json package-lock.json ./
RUN apk update && \
    apk upgrade && \
    apk add --no-cache make gcc g++ python openjdk11

RUN npm ci

# ソースコードと設定ファイルのコピー
COPY shadow-cljs.edn workbox-config.js ./
COPY src/ ./src/
COPY resources/ ./resources/

# プロダクションビルドの実行
RUN npm run build:prod

# 実行ステージ
FROM node:8.17-alpine

# 作業ディレクトリの作成
WORKDIR /usr/src/app

# ビルドステージから必要なファイルをコピー
COPY --from=build /usr/src/app/node_modules ./node_modules
COPY --from=build /usr/src/app/target ./target
COPY --from=build /usr/src/app/resources/public/prod ./resources/public/prod

# アプリケーションの実行
CMD ["node", "target/server/prod/js/compiled/server.js", "3000"]
```

### 5.2 CIビルドスクリプトの更新

必要に応じてCIビルドスクリプトも更新します。具体的な内容はプロジェクトのCIサービスに依存します。

## フェーズ6: 検証フェーズ

### 6.1 ローカル環境での検証

1. 開発環境でのビルドとテスト
   ```bash
   cd front-end
   npm install
   npm run build:dev
   npm start
   ```

2. プロダクション環境でのビルドとテスト
   ```bash
   cd front-end
   npm run build:prod
   npm start
   ```

### 6.2 自動テストの実行

既存の自動テストを実行し、移行後も正常に動作することを確認します。

### 6.3 問題の特定と修正

1. 移行中に発生した問題をリストアップ
2. 優先順位を付けて解決
3. 解決策をドキュメント化

## リスクと緩和策

### 潜在的なリスク

1. **既存コードとの互換性**
   - 緩和策: シムレイヤーを作成し、段階的に移行する

2. **パフォーマンスへの影響**
   - 緩和策: パフォーマンステストを実施し、必要に応じて最適化する

3. **デプロイ時の問題**
   - 緩和策: ステージング環境でテストし、ロールバック計画を準備する

## ロールバック計画

万が一、移行後に重大な問題が発生した場合のロールバック手順:

1. 元のビルドシステムでアプリケーションを再ビルド
2. 元のDockerイメージを使用してデプロイ
3. 問題を分析し、修正計画を作成

## タイムライン

1. フェーズ1 (準備): 1週間
2. フェーズ2 (シム実装): 1週間
3. フェーズ3 (クライアント移行): 2週間
4. フェーズ4 (サーバー移行): 2週間
5. フェーズ5 (ビルド改善): 1週間
6. フェーズ6 (検証): 1週間

合計: 約8週間（問題解決の時間を含む）

## 最終調整

- パフォーマンスチューニング
- 残りのワークアラウンドの除去

## Web Workerの実装方法

### Web Workerモジュールの設定

shadow-cljsのドキュメント「[User's Guide - Web Workers](https://shadow-cljs.github.io/docs/UsersGuide.html#_web_workers)」に基づき、Web Workerを`:browser`ターゲット内のモジュールとして実装します。

#### モジュール設定

```clojure
;; shadow-cljs.edn の :modules 設定
:modules {:cljs-base {...}
          :client {...}
          ;; 他のモジュール
          :worker {:entries [vr-match.worker]
                   :depends-on #{:cljs-base}
                   :web-worker true  ;; Web Workerとして扱う
                   :init-fn vr-match.worker/init}}
```

この設定により、`:worker`モジュールがWeb Workerとして適切に構成されます。`:web-worker true`フラグにより、クライアントモジュールから分離され、適切なWeb Workerコンテキストで動作するためのコードが生成されます。

#### Web Workerの呼び出し

クライアントコードからは以下のようにWeb Workerを初期化して使用します：

```clojure
;; vr-match.effects などのクライアントコード
(defn initialize-worker []
  (let [worker (js/Worker. "/js/compiled/worker.js")]
    (.addEventListener worker "message" 
                       (fn [event] 
                         ;; メッセージ処理
                         ))
    ;; Workerにメッセージを送信
    (.postMessage worker #js{:cmd "start" :data "some-data"})))
```

#### Worker側の実装

```clojure
;; src/cljs/vr_match/worker.cljs
(ns vr-match.worker)

(defn handle-message [event]
  (let [data (.-data event)
        cmd (.-cmd data)]
    (case cmd
      "start" (.postMessage js/self #js{:result "開始しました"})
      ;; その他のコマンド処理
      )))

(defn init []
  ;; メッセージ受信リスナーを設定
  (.addEventListener js/self "message" handle-message))
```

この方法の利点は、別途ビルド定義を作成する必要がなく、一つのビルド内でWeb Workerを管理できることです。また、コードの共有や依存関係の管理も容易になります。

### ディレクトリ構造の整理

Web Worker実装のためのディレクトリ構造も整理します：

1. **従来の構造：**
   - `src/cljs-worker/vr_match/worker.cljs` - 独立したワーカーコードを配置
   - `:worker`ビルド定義で別々にビルド

2. **新しい構造：**
   - `src/cljs/vr_match/worker.cljs` - 通常のソースディレクトリ内にワーカーコードを統合
   - `:client`ビルド内のモジュールとしてワーカーを管理

3. **移行手順：**
   - `src/cljs/vr_match/worker.cljs`を新規作成
   - `src/cljs-worker/vr_match/worker.cljs`の内容をベースに`init`関数を追加
   - `src/cljs-worker/vr_match/worker.cljs`を削除
   - 必要に応じて`src/cljs-worker`ディレクトリ自体も削除

これらの変更により、より統一されたディレクトリ構造が実現され、ワーカーコードの管理が容易になります。また、`shadow-cljs.edn`の`:source-paths`からも将来的には`"src/cljs-worker"`を削除できますが、他に依存するコードがないことを確認してから行うべきです。
