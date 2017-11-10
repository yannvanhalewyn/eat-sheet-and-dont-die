(ns sheet-bucket.controllers.session
  (:require [ring.util.response :refer [response]]
            [datomic.api :as d]))

(defn show [{:keys [db-conn]}]
  ;; Finds any user for now
  (response (dissoc (ffirst (d/q '[:find (pull ?user [*])
                                   :where [?user :user/email]]
                              (d/db db-conn)))
              :user/password-digest)))
