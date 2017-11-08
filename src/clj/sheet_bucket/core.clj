(ns sheet-bucket.core
  (:require [com.stuartsierra.component :as c]
            [sheet-bucket.components.db :as db]
            [sheet-bucket.components.web :as web]))

(defn new-system [config]
  (c/system-map
    :db (db/component (:db config))
    :web (c/using (web/component (:port config)) [:db])))