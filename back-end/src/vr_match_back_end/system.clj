(ns vr-match-back-end.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [vr-match-back-end.infra.datasource.example :refer [example-datasource-component]]
            [vr-match-back-end.infra.repository.example :refer [example-repository-component]]
            [vr-match-back-end.domain.usecase.example :refer [example-usecase-component]]
            [vr-match-back-end.app.my-webapp.handler :refer [my-webapp-handler-component]]
            [vr-match-back-end.app.my-webapp.endpoint :refer [my-webapp-endpoint-component]])
  (:gen-class))

(defn vr-match-back-end-system
  [{:keys [vr-match-back-end-example-port
           vr-match-back-end-my-webapp-port
           vr-match-client-origin] :as conf}]
  (component/system-map
    :example-datasource (example-datasource-component vr-match-back-end-example-port)
    :example-repository (component/using
                          (example-repository-component)
                          [:example-datasource])
    :example-usecase (component/using
                       (example-usecase-component)
                       [:example-repository])
    :my-webapp-handler (component/using
                         (my-webapp-handler-component)
                         [:example-usecase])
    :my-webapp-endpoint (component/using
                         (my-webapp-endpoint-component vr-match-back-end-my-webapp-port
                                                       vr-match-client-origin)
                          [:my-webapp-handler])))

(defn load-config []
  {:vr-match-client-origin (or (env :vr-match-client-origin) "http://localhost:8888")
   :vr-match-back-end-example-port (-> (or (env :vr-match-back-end-example-port) "8000") Integer/parseInt)
   :vr-match-back-end-my-webapp-port (-> (or (env :vr-match-back-end-my-webapp-port) "8080") Integer/parseInt)})

(defn -main []
  (component/start
    (vr-match-back-end-system (load-config))))
