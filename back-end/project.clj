(defproject vr-match-back-end "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-bom "0.2.0-SNAPSHOT"]]
  :bom {:import [[com.google.cloud/libraries-bom "23.0.0"]]}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "0.4.500"]
                 [org.clojure/data.codec "0.1.1"]
                 [org.clojure/test.check "0.10.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.36"]
                 [com.novemberain/pantomime "2.11.0"]
                 [digest "1.4.10"]
                 [environ "1.1.0"]
                 [clj-time "0.15.2"]
                 [com.stuartsierra/component "0.4.0"]
                 [ring "1.7.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [cheshire "5.11.0"]
                 [cljstache "2.0.6"]
                 [integrant "0.8.0"]
                 [org.clojure/tools.namespace "0.3.0"]
                 [com.walmartlabs/lacinia "0.33.0"]
                 [e85th/venia "0.2.5-1"]
                 [com.layerware/hugsql "0.4.9"]
                 [mysql/mysql-connector-java "8.0.33"]
                 [com.google.firebase/firebase-admin "8.1.0" :exclusions [[com.google.api-client/google-api-client]]]
                 [com.google.cloud/google-cloud-storage]
                 [com.google.auth/google-auth-library-oauth2-http]]
  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]
  :profiles
  {:dev {:source-paths ["src" "dev"]
         :dependencies [[integrant/repl "0.3.2"]]}
   :uberjar {:main vr-match-back-end.app.my-webapp.system
             :aot :all}})
