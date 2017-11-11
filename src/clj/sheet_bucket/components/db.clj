(ns sheet-bucket.components.db
  (:require [com.stuartsierra.component :as c]
            [datomic.api :as d]
            [io.rkn.conformity :as conform]))

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
      (println "Could not connect to database with url: " uri (.getMessage e)))))

(defrecord Db [uri]
  c/Lifecycle
  (start [this]
    (let [conn (connect uri)]
      (println "Migrating schema...")
      (conform/ensure-conforms conn (conform/read-resource "schema.edn"))
      (assoc this :conn (connect uri))))
  (stop [this]
    (dissoc this :conn)))

(def component ->Db)
