(ns umuses.main
  (:gen-class)
  (:require [umuses.core :as app]
            [umuses.config :refer [config]]
            [com.stuartsierra.component :as c]))

(def system (app/new-system config))

(defn -main [& args]
  (c/start system))
