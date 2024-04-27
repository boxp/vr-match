(ns vr-match-back-end.app.my-webapp.config
  (:require [integrant.core :as ig]))

(def config
  {:vr-match-back-end.app.my-webapp.env/client-origin {}
   :vr-match-back-end.app.my-webapp.env/port {}
   :vr-match-back-end.app.my-webapp.env/firebase-database-url {}
   :vr-match-back-end.app.my-webapp.env/firebase-service-account-key {}
   :vr-match-back-end.app.my-webapp.env/mysql-dbname {}
   :vr-match-back-end.app.my-webapp.env/mysql-user {}
   :vr-match-back-end.app.my-webapp.env/mysql-password {}
   :vr-match-back-end.app.my-webapp.env/mysql-hostname {}
   :vr-match-back-end.app.my-webapp.env/mysql-port {}
   :vr-match-back-end.app.my-webapp.env/cloud-storage-bucket-name {}
   :vr-match-back-end.infra.datasource.mysql/mysql-datasource
   {:dbname (ig/ref :vr-match-back-end.app.my-webapp.env/mysql-dbname)
    :user (ig/ref :vr-match-back-end.app.my-webapp.env/mysql-user)
    :password (ig/ref :vr-match-back-end.app.my-webapp.env/mysql-password)
    :hostname (ig/ref :vr-match-back-end.app.my-webapp.env/mysql-hostname)
    :port (ig/ref :vr-match-back-end.app.my-webapp.env/mysql-port) }
   :vr-match-back-end.infra.datasource.cloud-storage/cloud-storage-datasource
   {:bucket-name (ig/ref :vr-match-back-end.app.my-webapp.env/cloud-storage-bucket-name)}
   :vr-match-back-end.infra.datasource.firebase-admin/firebase-admin-datasource
   {:database-url (ig/ref :vr-match-back-end.app.my-webapp.env/firebase-database-url)
    :credential (ig/ref :vr-match-back-end.app.my-webapp.env/firebase-service-account-key)}
   :vr-match-back-end.infra.repository.image/image-repository
   {:mysql-datasource (ig/ref :vr-match-back-end.infra.datasource.mysql/mysql-datasource)
    :cloud-storage-datasource (ig/ref :vr-match-back-end.infra.datasource.cloud-storage/cloud-storage-datasource)}
   :vr-match-back-end.infra.repository.platform/platform-repository
   {:mysql-datasource (ig/ref :vr-match-back-end.infra.datasource.mysql/mysql-datasource)}
   :vr-match-back-end.infra.repository.user/user-repository
   {:mysql-datasource (ig/ref :vr-match-back-end.infra.datasource.mysql/mysql-datasource)
    :firebase-admin-datasource (ig/ref :vr-match-back-end.infra.datasource.firebase-admin/firebase-admin-datasource)}
   :vr-match-back-end.domain.usecase.approach/approach-usecase
   {:user-repository (ig/ref :vr-match-back-end.infra.repository.user/user-repository)}
   :vr-match-back-end.domain.usecase.auth/auth-usecase
   {:user-repository (ig/ref :vr-match-back-end.infra.repository.user/user-repository)}
   :vr-match-back-end.domain.usecase.image/image-usecase
   {:user-repository (ig/ref :vr-match-back-end.infra.repository.user/user-repository)
    :image-repository (ig/ref :vr-match-back-end.infra.repository.image/image-repository)}
   :vr-match-back-end.domain.usecase.platform/platform-usecase
   {:platform-repository (ig/ref :vr-match-back-end.infra.repository.platform/platform-repository)}
   :vr-match-back-end.domain.usecase.user/user-usecase
   {:user-repository (ig/ref :vr-match-back-end.infra.repository.user/user-repository)}
   :vr-match-back-end.app.my-webapp.resolvers/my-webapp-resolvers
   {:approach-usecase (ig/ref :vr-match-back-end.domain.usecase.approach/approach-usecase)
    :auth-usecase (ig/ref :vr-match-back-end.domain.usecase.auth/auth-usecase)
    :image-usecase (ig/ref :vr-match-back-end.domain.usecase.image/image-usecase)
    :platform-usecase (ig/ref :vr-match-back-end.domain.usecase.platform/platform-usecase)
    :user-usecase (ig/ref :vr-match-back-end.domain.usecase.user/user-usecase)}
   :vr-match-back-end.app.my-webapp.handler/my-webapp-handler
   {:my-webapp-resolvers (ig/ref :vr-match-back-end.app.my-webapp.resolvers/my-webapp-resolvers)}
   :vr-match-back-end.app.my-webapp.endpoint/endpoint
   {:my-webapp-handler (ig/ref :vr-match-back-end.app.my-webapp.handler/my-webapp-handler)
    :port (ig/ref :vr-match-back-end.app.my-webapp.env/port)
    :client-origin (ig/ref :vr-match-back-end.app.my-webapp.env/client-origin)}})

