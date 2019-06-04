(ns vr-match.db)

(def default-db
  {:approach {:list []}
   :auth {:sign-in-link {:error nil
                         :email ""}
          :sign-in-with-email {:error nil
                               :email-input-required? false}}
   :router {:key :loading
            :params {}}
   :api-endpoint ""
   :api-error nil
   :drawer {:open? false}
   :fetch-status {:sign-in-link :none
                  :sign-in-with-email :none}
   :history nil})
