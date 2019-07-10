(ns vr-match-back-end.app.my-webapp.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [vr-match-back-end.infra.datasource.cloud-storage :refer [map->CloudStorageDatasource]]
            [vr-match-back-end.infra.datasource.firebase-admin :refer [map->FirebaseAdminDatasourceComponent]]
            [vr-match-back-end.infra.datasource.mysql :refer [map->MysqlDatasourceComponent]]
            [vr-match-back-end.infra.repository.user :refer [map->UserRepositoryComponent]]
            [vr-match-back-end.infra.repository.image :refer [map->ImageRepository]]
            [vr-match-back-end.domain.usecase.auth :refer [map->AuthUsecaseComponent]]
            [vr-match-back-end.app.my-webapp.resolvers :refer [map->MyWebappResolversComponent]]
            [vr-match-back-end.app.my-webapp.handler :refer [my-webapp-handler-component]]
            [vr-match-back-end.app.my-webapp.endpoint :refer [my-webapp-endpoint-component]])
  (:gen-class))

(defn vr-match-back-end-system
  [{:keys [vr-match-back-end-my-webapp-port
           vr-match-back-end-firebase-database-url
           vr-match-firebase-service-account-key
           vr-match-mysql-dbname
           vr-match-mysql-user
           vr-match-mysql-password
           vr-match-client-origin
           vr-match-cloud-storage-bucket-name] :as conf}]
  (component/system-map
    :firebase-admin-datasource (map->FirebaseAdminDatasourceComponent
                                {:database-url vr-match-back-end-firebase-database-url
                                 :credential vr-match-firebase-service-account-key})
    :mysql-datasource (map->MysqlDatasourceComponent {:dbname vr-match-mysql-dbname
                                                      :user vr-match-mysql-user
                                                      :password vr-match-mysql-password})
    :cloud-storage-datasource (map->CloudStorageDatasource {:bucket-name vr-match-cloud-storage-bucket-name})
    :user-repository (component/using
                      (map->UserRepositoryComponent {})
                      [:firebase-admin-datasource
                       :mysql-datasource
                       :cloud-storage-datasource])
    :image-repository (component/using
                       (map->ImageRepository {})
                       [:cloud-storage-datasource])
    :auth-usecase (component/using
                   (map->AuthUsecaseComponent {})
                   [:user-repository])
    :my-webapp-resolvers (component/using
                          (map->MyWebappResolversComponent {})
                          [:auth-usecase])
    :my-webapp-handler (component/using
                         (my-webapp-handler-component)
                         [:my-webapp-resolvers])
    :my-webapp-endpoint (component/using
                         (my-webapp-endpoint-component vr-match-back-end-my-webapp-port
                                                       vr-match-client-origin)
                          [:my-webapp-handler])))

(defn load-config []
  {:vr-match-client-origin (or (env :vr-match-client-origin) "http://localhost:8888")
   :vr-match-back-end-my-webapp-port (-> (or (env :vr-match-back-end-my-webapp-port) "8080") Integer/parseInt)
   :vr-match-back-end-firebase-database-url (-> (or (env :vr-match-back-end-firebase-database-url) "https://vr-match.firebaseio.com"))
   :vr-match-firebase-service-account-key (env :vr-match-firebase-service-account-key)
   :vr-match-mysql-dbname (or (env :vr-match-mysql-dbname) "vr_match")
   :vr-match-mysql-user (or (env :vr-match-mysql-user) "root")
   :vr-match-mysql-password (env :vr-match-mysql-password)
   :vr-match-cloud-storage-bucket-name (or (env :vr-match-cloud-storage-bucket-name) "vr-match-staging")})

(defn -main []
  (component/start
    (vr-match-back-end-system (load-config))))
