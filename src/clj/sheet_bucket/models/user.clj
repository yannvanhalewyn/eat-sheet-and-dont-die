(ns sheet-bucket.models.user
  (:require [sheet-bucket.components.db :as db]))

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
