(ns vr-match.route
  (:require
   [re-frame.core :as re-frame]
   [vr-match.util :as util]
   [vr-match.events :as events]
   [secretary.core :as secretary :refer-macros [defroute]]))

;; prefixなし
(secretary/set-config! :prefix "/")

(def route-table
  {:example {:container #(resolve 'vr-match.example.container/box)
             :module-name :example}
   :welcome {:container #(resolve 'vr-match.welcome.container/welcome)
             :module-name :welcome}
   :approach {:container #(resolve 'vr-match.approach.container/approach)
              :module-name :approach}
   :profile {:container #(resolve 'vr-match.profile.container/profile)
             :module-name :profile}
   :register {:container #(resolve 'vr-match.auth.containers.register/register)
              :module-name :register}
   :email-register {:container #(resolve 'vr-match.auth.containers.email-register/email-register)
                    :module-name :email-register}
   :email-register-complete {:container #(resolve 'vr-match.auth.containers.email-register-complete/email-register-complete)
                    :module-name :email-register-complete}
   :wizard {:container #(resolve 'vr-match.wizard.container/wizard)
            :module-name :wizard}
   :favorite {:container #(resolve 'vr-match.favorite.container/favorite)
              :module-name :favorite}
   :matching {:container #(resolve 'vr-match.matching.container/matching)
              :module-name :matching}
   :myprofile {:container #(resolve 'vr-match.myprofile.container/myprofile)
               :module-name :myprofile}
   :mypage {:container #(resolve 'vr-match.mypage.container/mypage)
            :module-name :mypage}})

(defn- lazy-push
  [key params]
  (util/universal-load (-> route-table key :module-name) #(re-frame/dispatch-sync [::events/universal-push key params])))

;; ルーティング定義
(defroute root-path "/" []
  (lazy-push :welcome {}))

(defroute profile-path "/profile/:id" [id]
  (lazy-push :profile {:id id}))

(defroute register-path "/register" []
  (lazy-push :register {}))

(defroute email-register-path "/email-register" []
  (lazy-push :email-register {}))

(defroute email-register-complete-path "/email-register-complete" []
  (lazy-push :email-register-complete {}))

(defroute approach-path "/approach" []
  (lazy-push :approach {}))

(defroute wizard-path "/wizard" []
  (lazy-push :wizard {}))

(defroute favorite-path "/favorite" []
  (lazy-push :favorite {}))

(defroute matching-path "/matching" []
  (lazy-push :matching {}))

(defroute myprofile-path "/myprofile" []
  (lazy-push :myprofile {}))

(defroute mypage-path "/mypage" []
  (lazy-push :mypage {}))

(defroute not-found-path "*" []
  (lazy-push :approach {}))
