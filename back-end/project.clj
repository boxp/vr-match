(defproject vr-match-back-end "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "0.4.500"]
                 [org.clojure/data.codec "0.1.1"]
                 [org.clojure/test.check "0.9.0"]
                 [environ "1.1.0"]
                 [clj-time "0.15.1"]
                 [com.stuartsierra/component "0.4.0"]
                 [ring "1.7.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [cheshire "5.8.1"]
                 [org.clojure/tools.namespace "0.3.0"]
                 [com.walmartlabs/lacinia "0.33.0"]
                 [vincit/venia "0.2.5"]
                 [com.layerware/hugsql "0.4.9"]
                 [mysql/mysql-connector-java "8.0.16"]
                 [com.google.firebase/firebase-admin "6.8.1"]
                 [com.google.auth/google-auth-library-oauth2-http "0.16.2"]
                 [com.google.cloud/google-cloud-storage "1.81.0"]]
  :profiles
  {:dev {:source-paths ["src" "dev"]}
   :uberjar {:main vr-match-back-end.app.my-webapp.system}})
