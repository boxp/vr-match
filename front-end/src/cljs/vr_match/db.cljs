(ns vr-match.db)

(def default-db
  {:approach {:list []
              :in-favorite-user nil
              :show-matching-dialog false}
   :auth {:sign-in-link {:error nil
                         :email ""}
          :sign-in-with-email {:error nil
                               :email-input-required? false}
          :linked-provider-ids #{}}
   :mypage {:platform-options nil}
   :myprofile {}
   :profile {:partner nil}
   :favorite {:favorited-from-me-list {:edges []
                                       :pageInfo nil}}
   :matching {:list {:edges []
                     :pageInfo nil}}
   :wizard {:uploaded-image nil
            :platform-options nil}
   :router {:key :loading
            :params {}}
   :me nil
   :api-endpoint ""
   :api-error nil
   :drawer {:open? false}
   :fetch-status {:sign-in-link :none
                  :sign-in-with-email :none
                  :linked-provider-ids :none
                  :register-user :none
                  :login-user :none
                  :approach :none
                  :me :none
                  :mypage :none
                  :firebase :none
                  :wizard :none
                  :favorite :none
                  :matching :none
                  :profile :none}
   :history nil})
