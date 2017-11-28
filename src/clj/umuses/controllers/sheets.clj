(ns umuses.controllers.sheets
  (:require [datomic.api :as d]
            [umuses.models.sheet :as sheet]
            [umuses.models.user :as user]
            [umuses.socket-handler :refer [socket-handler]]))

(defmethod socket-handler :sheets/index
  [{:keys [?data ring-req]}]
  (user/sheets (:db-conn ring-req) (:user-id ?data)))

(defmethod socket-handler :sheets/show
  [{:keys [?data ring-req]}]
  (sheet/find (:db-conn ring-req) ?data))

(defmethod socket-handler :sheets/create
  [{:keys [?data ring-req]}]
  (sheet/create! (:db-conn ring-req) ?data))
