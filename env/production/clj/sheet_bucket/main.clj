(ns sheet-bucket.main
  (:gen-class)
  (:require [sheet-bucket.core :as app]
            [sheet-bucket.config :refer [config]]
            [com.stuartsierra.component :as c]))

(def system (app/new-system config))

(defn -main [& args]
  (c/start system))
