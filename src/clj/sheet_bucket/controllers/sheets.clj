(ns sheet-bucket.controllers.sheets
  (:require [datomic.api :as d]
            [sheet-bucket.models.sheet :as sheet]
            [sheet-bucket.models.user :as user]
            [sheet-bucket.socket-handler :refer [socket-handler]]))

(defmethod socket-handler :sheets/index
  [{:keys [?data ring-req]}]
  (user/sheets (:db-conn ring-req) (:user-id ?data)))

(defmethod socket-handler :sheets/show
  [{:keys [?data ring-req]}]
  (sheet/find (:db-conn ring-req) ?data))

(defmethod socket-handler :sheets/create
  [{:keys [?data ring-req]}]
  (sheet/create! (:db-conn ring-req) ?data))

(defmethod socket-handler :sheets/destroy
  [{:keys [?data ring-req]}]
  (let [result (d/transact (:db-conn ring-req) [[:db.fn/retractEntity ?data]])]
    {:success true :removed-id ?data}))
