(ns sheet-bucket.models.user
  (:require [datomic.api :as d]
            [sheet-bucket.components.db :as db]))

(defn create! [conn {:keys [first-name last-name email password]}]
  (db/transact! conn [{:user/first-name first-name
                       :user/last-name last-name
                       :user/email email
                       :user/password-digest password}]))

(defn find-by-email [db email]
  (db/pull db '[*] [:user/email email]))

(defn sheets-for-user [db user]
  (:playlist/sheets
   (db/pull db user '[{:playlist/sheets [*]}])))

(defn sheets
  "Returns all the sheets (basic info) for given user."
  [db-conn user-id]
  (flatten (d/q '[:find (pull ?sheet [:db/id :sheet/artist :sheet/title])
                  :in $ ?user
                  :where [?user :playlist/sheets ?sheet]]
             (d/db db-conn)
             user-id)))
