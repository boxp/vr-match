(defn modules
  [output-dir]
  {:example {:entries #{"vr-match.example.container"}
             :output-to (str output-dir "/example.js")
             :depends-on #{:client}}
   :approach {:entries #{"vr-match.approach.container"}
              :output-to (str output-dir "/approach.js")
              :depends-on #{:client}}
   :profile {:entries #{"vr-match.profile.container"}
             :output-to (str output-dir "/profile.js")
             :depends-on #{:client}}
   :login {:entries #{"vr-match.login.container"}
           :output-to (str output-dir "/login.js")
           :depends-on #{:client}}
   :register {:entries #{"vr-match.register.container"}
              :output-to (str output-dir "/register.js")
              :depends-on #{:client}}
   :wizard {:entries #{"vr-match.wizard.container"}
            :output-to (str output-dir "/wizard.js")
            :depends-on #{:client}}
   :favorite {:entries #{"vr-match.favorite.container"}
              :output-to (str output-dir "/favorite.js")
              :depends-on #{:client}}
   :matching {:entries #{"vr-match.matching.container"}
              :output-to (str output-dir "/matching.js")
              :depends-on #{:client}}
   :myprofile {:entries #{"vr-match.myprofile.container"}
               :output-to (str output-dir "/myprofile.js")
               :depends-on #{:client}}
   :mypage {:entries #{"vr-match.mypage.container"}
            :output-to (str output-dir "/mypage.js")
            :depends-on #{:client}}
   ;; 分割されたモジュールをロードするために最低限必要なモジュール
   ;; モジュールの分割を行うと必ずこのモジュールが分割されるので出力先ファイル名だけ変更している
   :cljs-base {:output-to (str output-dir "/cljs_base.js")}
   :client {:entries #{"vr-match.client"}
            :output-to (str output-dir "/app.js")
            :depends-on #{:cljs-base}}})

(defproject vr-match "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [secretary "1.2.3"]
                 [kibu/pushy "0.3.8"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [cljsjs/material-ui "3.1.1-0"]
                 [re-graph "0.1.8"]
                 [vincit/venia "0.2.5"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs" "src/cljs-client" "src/cljs-server"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "resources/public/prod/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.10"]
                   [cider/piggieback "0.3.5"]
                   [figwheel-sidecar "0.5.18"]
                   [day8.re-frame/re-frame-10x "0.4.0"]]
    :plugins      [[lein-figwheel "0.5.18"]
                   [lein-cljfmt "0.6.2"]]}
   :prod {}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "src/cljs-client"]
     :figwheel     {:on-jsload "vr-match.client/remount-for-figwheel"}
     :compiler     {:main vr-match.client
                    :output-dir      "resources/public/js/compiled"
                    :asset-path      "/static/js/compiled"
                    :source-map-timestamp true
                    :preloads [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    :npm-deps false
                    :modules ~(modules "resources/public/js/compiled")}}

    {:id           "prod"
     :source-paths ["src/cljs" "src/cljs-client"]
     :compiler     {:main vr-match.client
                    :closure-defines {vr-match.effects/worker-resource-path "/static/worker/js/compiled/worker.js"
                                      goog.DEBUG false}
                    :output-dir      "resources/public/prod/js/compiled"
                    :asset-path      "/static/js/compiled"
                    :optimizations   :advanced
                    :pretty-print    false
                    :npm-deps false
                    :modules ~(modules "resources/public/prod/js/compiled")}}
    {:id           "server-dev"
     :source-paths ["src/cljs" "src/cljs-server"]
     :compiler     {:main vr-match.server
                    :output-dir      "target/server/js/compiled"
                    :output-to      "target/server/js/compiled/server.js"
                    :preamble ["ssr-preamble.js"]
                    :asset-path      "target/server/js/compiled"
                    :source-map-timestamp true
                    :target :nodejs
                    :npm-deps false
                    :closure-defines {vr-match.server/dev? true
                                      vr-match.server/static-file-path "resources/public/"}
                    :pretty-print    true}}
    {:id           "server-prod"
     :source-paths ["src/cljs" "src/cljs-server"]
     :compiler     {:main vr-match.server
                    :output-dir      "target/server/prod/js/compiled"
                    :output-to      "target/server/prod/js/compiled/server.js"
                    :preamble ["ssr-preamble.js"]
                    :asset-path      "target/server/prod/js/compiled"
                    :target :nodejs
                    :optimizations   :simple
                    :npm-deps false
                    :process-shim false
                    :closure-defines {vr-match.server/dev? false
                                      vr-match.server/static-file-path "resources/public/prod/"
                                      goog.DEBUG false}
                    :pretty-print    false}}
     {:id           "worker-dev"
      :source-paths ["src/cljs-worker"]
      :compiler     {:main vr-match.worker
                     :output-dir "resources/public/worker/js/compiled"
                     :output-to "resources/public/worker/js/compiled/worker.js"
                     :asset-path "/static/worker/js/compiled"
                     :target :webworker
                     :source-map-timestamp true
                     :optimizations :simple
                     :npm-deps false}}
    {:id           "worker-prod"
     :source-paths ["src/cljs-worker"]
     :compiler     {:main vr-match.worker
                    :output-dir "resources/public/prod/worker/js/compiled"
                    :output-to "resources/public/prod/worker/js/compiled/worker.js"
                    :asset-path "/static/worker/js/compiled"
                    :target :webworker
                    :optimizations :advanced
                    :npm-deps false}}]})
