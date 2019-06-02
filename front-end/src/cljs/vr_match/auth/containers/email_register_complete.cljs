(ns vr-match.auth.containers.email-register-complete
  (:require
   [re-frame.core :as re-frame]
   [vr-match.util :as util]
   [vr-match.events :as events]
   [vr-match.auth.components.email-register-complete :as component]))

(defn- handle-initialize
  [])

(defn email-register-complete
  [params]
  [component/email-register-complete {:handleInitialize handle-initialize}])

(util/universal-set-loaded! :email-register-complete)
