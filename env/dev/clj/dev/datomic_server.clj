(ns dev.datomic-server
  (:require [sheet-bucket.config :refer [config]]
            [dev.util :as util]
            [com.stuartsierra.component :as c]))

(defrecord BackgroundProcess [name args]
  c/Lifecycle
  (start [component]
    (if-not (:process component)
      (do
        (println (format "Starting %s..." name))
        (let [process (util/exec args)])
        (assoc component
          :process (util/exec args)))
      component))
  (stop [component]
    (when-let [process (:process component)]
      (println (format "Stopping %s..." name))
      (.destroy process))
    component))

(def datomic-dir (util/expand-home (:datomic-install-dir config)))
(def transactor-props (util/expand-home (:datomic-dev-transactor-properties config)))
(def datomic-bin #(str datomic-dir "/bin/" %))

(defn transactor []
  (->BackgroundProcess "Datomic transactor"
    [(datomic-bin "transactor") transactor-props]))

(defn peer-server []
  (->BackgroundProcess "Datomic peer server"
    [(datomic-bin "run")
     "-m" "datomic.peer-server"
     "-h" "localhost"
     "-p" "8998"
     "-a" "key,secret"
     "-d" "sheet-bucket" "datomic:dev://localhost:4334/sheet-bucket"]))

(defn console []
  (->BackgroundProcess "Datomic console"
    [(datomic-bin "console")
     "-p" "5445"
     "sheet-bucket" "datomic:dev://localhost:4334"]))

(defn component []
  (c/system-map
    :transactor (transactor)
    :peer-server (peer-server)
    :console (console)))
