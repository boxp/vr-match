(ns vr-match-back-end.app.my-webapp.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [vr-match-back-end.app.my-webapp.config :refer [config]])
  (:gen-class))

(defn -main []
  (ig/load-namespaces config)
  (ig/init config))
