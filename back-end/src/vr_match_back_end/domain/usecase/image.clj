(ns vr-match-back-end.domain.usecase.image
  (:require
   [integrant.core :as ig]
   [clojure.spec.alpha :as s]
   [com.stuartsierra.component :as component]
   [vr-match-back-end.infra.repository.image :as rimage]
   [vr-match-back-end.infra.repository.user :as ruser]
   [vr-match-back-end.domain.entity.image :as eimage]
   [vr-match-back-end.domain.entity.user :as euser]))

(s/def ::base64-string string?)
(s/def ::session ::euser/session_cookie)
(s/fdef upload-image
  :args (s/cat :c (s/keys :req-un [::rimage/image-repository])
               :params (s/keys :req-un [::base64-string
                                        ::session]))
  :ret (s/keys :req-un [::eimage/id
                        ::eimage/url]))
(defn upload-image
  [{:keys [user-repository image-repository]}
   {:keys [base64-string session]}]
  (let [user-id (ruser/get-user-id-by-session
                 user-repository
                 session)]
    (rimage/add-image
     image-repository
     base64-string)))

(defmethod ig/init-key ::image-usecase [_ u] u)

(defmethod ig/halt-key! ::image-usecase [_ _] nil)

(defmethod ig/pre-init-spec ::image-usecase [_]
  (s/keys :req-un [:ruser/user-repository :rimage/image-repository]))

