(ns sheet-bucket.models.user
  (:require [sheet-bucket.db :as db]
            [datomic.client :as client]
            [clojure.core.async :refer [<!!]]))

(defn create! [conn {:keys [first-name last-name email password]}]
  (db/transact! conn [{:user/first-name first-name
                       :user/last-name last-name
                       :user/email email
                       :user/password-digest password}]))

(defn find-by-email [db email]
  (<!! (client/pull db {:eid [:user/email email]
                        :selector '[*]})))

(defn sheets-for-user [db user]
  (:playlist/sheets
   (<!! (client/pull db {:eid user
                         :selector '[{:playlist/sheets [*]}]}))))
