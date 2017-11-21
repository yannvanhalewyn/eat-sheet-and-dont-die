(ns sheet-bucket.controllers.session
  (:require [datomic.api :as d]))

(defn show [db-conn]
  ;; Finds any user for now
  (dissoc (ffirst (d/q '[:find (pull ?user [*])
                         :where [?user :user/email]]
                    (d/db db-conn)))
    :user/password-digest))
