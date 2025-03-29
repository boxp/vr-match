# SSRサーバーの現状分析

## 現在の問題点

VR-Matchプロジェクトのフロントエンド部分、特にSSR（Server-Side Rendering）サーバーには以下の主要な問題があります：

1. **パッケージの二重管理**
   - 同じJavaScriptパッケージがClojureScriptの依存関係（project.clj）とnpm（package.json）の両方で管理されています
   - これにより、パッケージの更新が困難になり、バージョンの不一致が発生しやすくなっています

2. **ハックとワークアラウンド**
   - Dockerfileでビルド後のJavaScriptファイルを直接書き換えています
   - `resources/ssr-preamble.js`でグローバル変数の設定を手動で行っています
   - `client.cljs`でReact JSSの定数をハードコードしています

3. **環境の互換性問題**
   - ClojureScriptのGoogle Closureコンパイラとnode.jsの環境の違いを解決するための複雑なワークアラウンドが存在しています

## 詳細な技術的状況

### ビルドプロセスの問題

1. **ビルド後の強制的な書き換え**
   - Dockerfileの最終ステージでは、以下のようなコードでSSRサーバーのJSファイルを強制的に書き換えています：
   ```
   RUN sed -i -e 's/goog.global.React/global.React/g' -e 's/goog.global.ReactDOM/global.ReactDOM/g' -e 's/goog.global.createReactClass/global.createReactClass/g' target/server/prod/js/compiled/server.js
   ```
   - package.jsonの`build:prod:server`スクリプトでも同様の書き換えが行われています

2. **プリアンブルによるグローバル変数の設定**
   - `resources/ssr-preamble.js`では、Node.js環境にReactなどのグローバル変数を手動で設定しています
   - Material-UIのuniversal対応のために、windowやnavigatorなどのブラウザ固有のオブジェクトをモックしています

### フロントエンドコードの問題

1. **クライアント側のワークアラウンド**
   - `client.cljs`では、React JSSに関連する定数をハードコードしています：
   ```clojure
   (def sheet-options "6fc570d6bd61383819d0f9e7407c452d")
   ```
   - JSSContextProviderの実装も非標準的な方法で行われています

2. **サーバー側のコード**
   - `server.cljs`では、expressサーバーとReactのSSRを連携させるために複雑なコードが使われています
   - Material-UIのSSRサポートのために、sheetsRegistryやsheetsManagerなどの特殊なオブジェクトを使用しています

### 依存関係の管理

1. **ClojureScriptの依存関係**
   - project.cljでは以下のようなフロントエンド関連の依存関係が定義されています：
     - reagent "0.8.1"（ReactのClojureScriptラッパー）
     - re-frame "0.10.8"（状態管理ライブラリ）
     - cljsjs/material-ui "3.9.3-0"（Material-UIのClojureScriptラッパー）
     - cljsjs/firebase "5.7.3-1"（FirebaseのClojureScriptラッパー）

2. **npmの依存関係**
   - package.jsonでは以下のようなフロントエンド関連の依存関係が定義されています：
     - react "16.14.0"
     - react-dom "16.14.0"
     - react-jss "8.6.1"
     - create-react-class "15.7.0"
     - firebase "5.7.3"

3. **バージョンの不一致**
   - 例えば、firebase（npm）は5.7.3ですが、cljsjs/firebase（ClojureScript）は5.7.3-1となっており、完全に一致しているか確認が必要です

## 問題の影響

1. **メンテナンス性の低下**
   - パッケージの更新が困難になり、セキュリティ更新や新機能の追加が滞りやすくなっています
   - ワークアラウンドの存在により、コードの理解や変更が難しくなっています

2. **安定性の問題**
   - ハックによって維持されている部分があるため、環境の変更やパッケージの更新でアプリケーションが壊れる可能性があります

3. **開発効率の低下**
   - 開発者は特殊なビルドプロセスやワークアラウンドを理解する必要があり、学習コストが高くなっています

## 改善の方向性

1. **パッケージ管理の一元化**
   - ClojureScriptとnpmの依存関係を整理し、重複を排除する

2. **標準的なSSR実装への移行**
   - 標準的なClojureScript+React+Node.jsのSSRパターンを採用する
   - ハックやワークアラウンドを排除し、メンテナンス性を向上させる

3. **ビルドプロセスの改善**
   - ビルド後のファイル書き換えを不要にする
   - プリアンブルファイルを適切に整理し、必要最小限にする

## 改善策の詳細

調査に基づき、以下の具体的な改善策を提案します。

### 1. shadow-cljsへの移行

現在のClojureScriptビルドシステム（lein + cljsbuild）からshadow-cljsへ移行することで、多くの問題が解決できます。

#### shadow-cljsの利点
- npmパッケージと直接統合できるため、CLJSJSパッケージが不要になります
- 標準的なNode.js環境との連携が容易になります
- ビルド後の強制的な書き換えが不要になります
- より現代的なJavaScriptモジュールシステムのサポートにより、互換性の問題が減少します

#### 移行ステップ
1. `shadow-cljs.edn`ファイルを作成し、ビルド設定を移行
2. プロジェクト構成を更新
   ```clojure
   ;; shadow-cljs.edn の例
   {:source-paths ["src/cljs" "src/cljs-client" "src/cljs-server" "src/cljs-worker"]
    :dependencies [[reagent "0.10.0"]
                   [re-frame "1.2.0"]]
    :builds
    {:client {...}
     :server {...}
     :worker {...}}}
   ```

3. CLJSJSパッケージをnpmパッケージに置き換え
   ```bash
   # CLJSJSパッケージの代わりに直接npmからインストール
   npm install react react-dom create-react-class firebase
   ```

#### Web Workerのビルド方法

shadow-cljsのドキュメント「[User's Guide - Web Workers](https://shadow-cljs.github.io/docs/UsersGuide.html#_web_workers)」セクションに基づき、Web Workerの適切なビルド方法を説明します。shadow-cljsでは、Web Workerを実装する方法として、`:modules`定義内に`:web-worker true`を設定する方法が推奨されています。

1. **モジュールとしてのWeb Worker実装（推奨）**
   ```clojure
   ;; shadow-cljs.edn の例
   {:builds
    {:client {:target :browser
              :output-dir "resources/public/js/compiled"
              :modules {:main {:entries [vr-match.client]
                               :init-fn vr-match.client/init}
                        :worker {:entries [vr-match.worker]
                                 :web-worker true  ;; このモジュールをWeb Workerとして扱う
                                 :init-fn vr-match.worker/init}}
              :compiler-options {:optimizations :advanced}}}}
   ```

   この方法では、`:worker`モジュールが自動的にWeb Workerとして適切に構成されます。クライアントコードからは以下のように使用します：

   ```clojure
   ;; クライアント側のコード例
   (defn init-worker []
     (let [worker (js/Worker. "/js/compiled/worker.js")]
       (.addEventListener worker "message" 
                          (fn [event] 
                            (js/console.log "Worker response:" (.-data event))))
       (.postMessage worker #js{:cmd "start" :data "some-data"})))
   ```

2. **Worker側のコード例**
   ```clojure
   ;; src/cljs/vr_match/worker.cljs
   (ns vr-match.worker)
   
   (defn handle-message [event]
     (let [data (.-data event)
           cmd (.-cmd data)]
       (case cmd
         "start" (.postMessage js/self #js{:result "started"})
         ;; その他のコマンド処理
         )))
   
   (defn init []
     ;; メッセージ受信リスナーを設定
     (.addEventListener js/self "message" handle-message))
   ```

この方法の利点：
- より統合的なアプローチで、単一のビルド設定内でWeb Workerを管理できます
- モジュール間の依存関係を適切に管理しやすくなります
- コードの共有や再利用が容易になります

特に、shadow-cljsドキュメントでは、「Web Workersの使用」セクションで`:web-worker`オプションを使用してモジュールをWeb Workerとして指定する方法が説明されています。これにより、別途ビルドを定義するよりも簡潔で管理しやすい構成が可能になります。

### 2. Material-UIのSSRサポート改善

Material-UIのSSR実装を現代的な方法に更新します。

#### 改善ステップ
1. 最新のMaterial-UIのSSRパターンを採用
   ```clojure
   ;; server.cljs
   (ns vr-match.server
     (:require
       ["react" :as react]
       ["react-dom/server" :as react-dom-server]
       ["@mui/material/styles" :refer [ThemeProvider ServerStyleSheets]]
       [vr-match.components :refer [app]]))
   
   (defn render-to-string [component]
     (let [sheets (ServerStyleSheets.)
           html (.renderToString react-dom-server
                  (.collect sheets
                    (react/createElement ThemeProvider
                      #js{:theme theme}
                      (app))))
           css (.toString sheets)]
       {:html html :css css}))
   ```

2. プリアンブルファイルの削除または簡素化
   - 現在の`ssr-preamble.js`を段階的に削除し、必要な部分だけを残す

3. クライアント側のコードも対応するように更新
   ```clojure
   ;; client.cljs
   (defn main []
     (react/useEffect
       (fn []
         (let [jss-styles (.querySelector js/document "#jss-server-side")]
           (when jss-styles
             (.removeChild (.-parentElement jss-styles) jss-styles)))
         (fn []))
       #js[])
     
     (reagent/as-element
       [mui/ThemeProvider {:theme theme}
        [app]]))
   ```

### 3. ビルドプロセスの改善

Dockerfileとビルドスクリプトをシンプルかつメンテナンス性の高いものに更新します。

#### 改善ステップ
1. Dockerfileの改善
   ```dockerfile
   # 開発環境
   FROM node:14-alpine as build
   
   WORKDIR /app
   COPY package.json package-lock.json ./
   RUN npm ci
   
   COPY shadow-cljs.edn ./
   COPY src ./src
   
   # shadow-cljsでビルド（書き換えが不要になる）
   RUN npx shadow-cljs release client server worker
   
   # 実行環境
   FROM node:14-alpine
   WORKDIR /app
   COPY --from=build /app/node_modules /app/node_modules
   COPY --from=build /app/public /app/public
   
   CMD ["node", "public/js/server.js"]
   ```

2. ビルドスクリプトの更新
   - package.jsonのスクリプトをshadow-cljsに対応したものに変更
   ```json
   {
     "scripts": {
       "build": "shadow-cljs release client server worker",
       "dev": "shadow-cljs watch client server worker",
       "start": "node public/js/server.js"
     }
   }
   ```

### 4. 依存関係の一元管理

依存関係を一元管理するための戦略を実装します。

#### 改善ステップ
1. CLJSJSパッケージの依存関係を削除
   - project.cljからcljsjs/reactなどのパッケージを削除

2. npmを依存関係の主要な管理方法として採用
   - 必要なパッケージを全てnpmで管理
   - バージョンを明示的に指定し、一貫性を確保

3. 必要に応じてshim（互換レイヤー）を実装
   - CLJSJSパッケージに依存しているコードのために一時的なshimを作成
   ```clojure
   ;; src/cljsjs/react.cljs
   (ns cljsjs.react
     (:require ["react" :as react]))
   
   ;; グローバル変数として公開（移行期間のみ）
   (js/goog.exportSymbol "React" react)
   ```

### 5. 段階的な移行計画

一度にすべての変更を行うのではなく、段階的な移行計画を策定します。

#### 移行フェーズ
1. **準備フェーズ**
   - shadow-cljsの導入と基本的な設定
   - 依存関係の整理と更新

2. **クライアント側の移行**
   - クライアントコードをshadow-cljs対応に更新
   - Material-UIのクライアント側の実装を改善

3. **サーバー側の移行**
   - SSRサーバーコードを更新
   - プリアンブルファイルの削除または簡素化

4. **ビルドプロセスの改善**
   - Dockerfileの更新
   - CI/CDパイプラインの更新

5. **最終調整**
   - パフォーマンスチューニング
   - 残りのワークアラウンドの除去
