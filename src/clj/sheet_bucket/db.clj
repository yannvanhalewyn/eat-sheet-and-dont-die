(ns sheet-bucket.db
  (:require [datomic.client :as d]
            [clojure.core.async :refer [<!!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def conn (<!! (d/connect {:db-name "sheet-bucket"
                           :account-id d/PRO_ACCOUNT
                           :endpoint "localhost:8998"
                           :secret "secret"
                           :access-key "key"
                           :service "peer-server"
                           :region "none"})))

(defn- read-schema! []
  (->> (io/file "resources/schema.edn") slurp edn/read-string))

(defn transact! [conn tx-data]
  (<!! (d/transact conn {:tx-data tx-data})))

(defn load-schema! [conn]
  (transact! conn (read-schema!)))
