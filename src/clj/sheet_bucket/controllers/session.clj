(ns sheet-bucket.controllers.session
  (:require [sheet-bucket.socket-handler :refer [socket-handler]]
            [datomic.api :as d]))

(defmethod socket-handler :users/me
  [{:keys [?data ring-req]}]
  (dissoc (ffirst (d/q '[:find (pull ?user [*])
                         :where [?user :user/email]]
                    (d/db (:db-conn ring-req))))
    :user/password-digest))
