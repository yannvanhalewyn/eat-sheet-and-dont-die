(ns sheet-bucket.components.db
  (:require [com.stuartsierra.component :as c]
            [datomic.client :as client]
            [clojure.core.async :refer [<!!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def DEFAULT_CONFIG {:db-name "sheet-bucket"
                     :account-id client/PRO_ACCOUNT
                     :endpoint "localhost:8998"
                     :secret "secret"
                     :access-key "key"
                     :service "peer-server"
                     :region "none"})

(defn- read-schema! []
  (->> (io/file "resources/schema.edn") slurp edn/read-string))

(defn transact! [conn tx-data]
  (<!! (client/transact conn {:tx-data tx-data})))

(defn load-schema! [conn]
  (transact! conn (read-schema!)))

(defrecord Db [config]
  c/Lifecycle
  (start [this]
    (assoc this :conn (<!! (client/connect config))))
  (stop [this]
    (dissoc this :conn)))

(defn component [config]
  (Db. (merge config DEFAULT_CONFIG)))

;; Utilities
;; =========

(defn retract-entity! [conn eid]
  (transact! conn [[:db.fn/retractEntity eid]]))
