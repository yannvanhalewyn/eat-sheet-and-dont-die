(ns sheet-bucket.components.db
  (:require [com.stuartsierra.component :as c]
            [datomic.api :as d]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

;; Helper fns
;; ==========

(defn transact! [conn tx-data]
  (d/transact conn tx-data))

(defn pull [db eid selector]
  (d/pull db selector eid))

(defn- read-schema! []
  (->> (io/file "resources/schema.edn") slurp edn/read-string))

(defn load-schema! [conn]
  (transact! conn (read-schema!)))

(defn retract-entity! [conn eid]
  (transact! conn [[:db.fn/retractEntity eid]]))

;; Component
;; =========

(defrecord Db [uri]
  c/Lifecycle
  (start [this]
    (try
      (d/create-database uri)
      (assoc this :conn (d/connect uri))
      (catch Exception e
        (println "Could not connect to database with url: " uri (.getMessage e))
        this)))
  (stop [this]
    (dissoc this :conn)))

(def component ->Db)
