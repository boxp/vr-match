(ns vr-match.db)

(def default-db
  {:approach {:list []}
   :router {:key :loading
            :params {}}
   :api-endpoint ""
   :api-error nil
   :drawer {:open? false}
   :fetch-status {}
   :history nil})
