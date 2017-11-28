(ns umuses.components.db
  (:require [com.stuartsierra.component :as c]
            [datomic.api :as d]
            [io.rkn.conformity :as conform]
            [taoensso.timbre :as timbre]))

;; Helper fns
;; ==========

(defn transact! [conn tx-data]
  (d/transact conn tx-data))

(defn pull [db eid selector]
  (d/pull db selector eid))

(defn retract-entity! [conn eid]
  (transact! conn [[:db.fn/retractEntity eid]]))

;; Component
;; =========

(defn connect [uri]
  (try
    (d/create-database uri)
    (d/connect uri)
    (catch Exception e
      (timbre/error e "Could not connect to database with url: " uri))))

(defrecord Db [uri]
  c/Lifecycle
  (start [this]
    (when-let [conn (connect uri)]
      (timbre/info "Migrating schema...")
      (try
        (conform/ensure-conforms conn (conform/read-resource "schema.edn"))
        (catch Exception e
          (timbre/error e "Could not migrate schema.")))
      (assoc this :conn (connect uri))))
  (stop [this]
    (dissoc this :conn)))

(def component ->Db)
