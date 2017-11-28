(ns umuses.core
  (:require [com.stuartsierra.component :as c]
            [umuses.components.channel-sockets :as chsk]
            [umuses.components.db :as db]
            [umuses.components.tx-report-monitor :as tx-report-monitor]
            [umuses.components.web :as web]))

(defn new-system [config]
  (c/system-map
    :db (db/component (:db-url config))
    :web (c/using
           (web/component (:port config))
           [:db :channel-sockets])
    :channel-sockets (c/using
                       (chsk/component)
                       [:tx-report-monitor])
    :tx-report-monitor (c/using
                         (tx-report-monitor/component)
                         [:db])))
