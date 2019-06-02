(ns vr-match.db)

(def default-db
  {:approach {:list []}
   :auth {:sign-in-link {:error nil
                         :email ""}}
   :router {:key :loading
            :params {}}
   :api-endpoint ""
   :api-error nil
   :drawer {:open? false}
   :fetch-status {}
   :history nil})
