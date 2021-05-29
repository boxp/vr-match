(ns user
  (:require
   [clojure.spec.test.alpha :as stest]
   [environ.core :refer [env]]
   [com.stuartsierra.component :as component]
   [clojure.tools.namespace.repl :refer (refresh)]
   [com.walmartlabs.lacinia :as lacinia]
   [venia.core :as venia]
   [integrant.core :as ig]
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   ;; [vr-match-back-end.app.my-webapp.system :refer [vr-match-back-end-system load-config]]
   [vr-match-back-end.app.my-webapp.config :refer [config]]))

(integrant.repl/set-prep!
 (constantly config))

;; (def system nil)

;; (defn init []
;;   (alter-var-root #'system
;;                   (constantly (vr-match-back-end-system (load-config)))))
;; 
;; (defn start []
;;   (alter-var-root #'system component/start))
;; 
;; (defn stop []
;;   (alter-var-root #'system
;;                   (fn [s] (when s (component/stop s)))))
;; 
;; (defn go []
;;   (stest/instrument)
;;   (init)
;;   (start))
;; 
;; (defn reset []
;;   (stop)
;;   (refresh :after 'user/go))
;; 
;; (defn q
;;   ([query] (q query nil))
;;   ([query variables]
;;    (lacinia/execute (-> system :my-webapp-handler :graphql-schema)
;;                     (venia/graphql-query query)
;;                     variables
;;                     (-> system :my-webapp-resolvers))))

