(ns umuses.controllers.session
  (:require [datomic.api :as d]
            [umuses.socket-handler :refer [socket-handler]]))

(defmethod socket-handler :users/me
  [{:keys [?data ring-req]}]
  (dissoc (ffirst (d/q '[:find (pull ?user [*])
                         :where [?user :user/email]]
                    (d/db (:db-conn ring-req))))
    :user/password-digest))
