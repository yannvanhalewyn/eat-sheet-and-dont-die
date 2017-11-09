(ns dev.datomic-server
  (:require [dev.background-process :as bp]
            [dev.util :as util]
            [sheet-bucket.config :refer [config]]
            [com.stuartsierra.component :as c]))

(def datomic-dir (util/expand-home (:datomic-install-dir config)))
(def transactor-props (util/expand-home (:datomic-dev-transactor-properties config)))
(def datomic-bin #(str datomic-dir "/bin/" %))

(defn transactor []
  (bp/->BackgroundProcess "Datomic transactor"
    [(datomic-bin "transactor") transactor-props]))

(defn peer-server []
  (bp/->BackgroundProcess "Datomic peer server"
    [(datomic-bin "run")
     "-m" "datomic.peer-server"
     "-h" "localhost"
     "-p" "8998"
     "-a" "key,secret"
     "-d" "sheet-bucket" "datomic:dev://localhost:4334/sheet-bucket"]))

(defn console []
  (bp/->BackgroundProcess "Datomic console"
    [(datomic-bin "console")
     "-p" "5445"
     "sheet-bucket" "datomic:dev://localhost:4334"]))

(defn component []
  (c/system-map
    :transactor (transactor)
    :peer-server (peer-server)
    :console (console)))
