(ns umuses.components.channel-sockets
  (:require [com.stuartsierra.component :as c]
            [clojure.core.async :as a]
            [umuses.socket-handler :as sh]
            [umuses.controllers.session]
            [umuses.controllers.sheets]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [taoensso.timbre :as timbre]))

(defn broadcast [chsk msg]
  (doseq [uid (:any @(:connected-uids chsk))]
    ((:send-fn chsk) uid msg)))

(def SENTE_KEYS [:ajax-get-or-ws-handshake-fn
                 :ajax-post-fn :ch-recv :send-fn])

(defrecord ChannelSockets [tx-report-monitor]
  c/Lifecycle
  (start [this]
    (timbre/info "Starting Channel Sockets listener...")
    (let [chsk (sente/make-channel-socket! (get-sch-adapter) {})
          stop-fn (sente/start-chsk-router! (:ch-recv chsk) #'sh/handler)]

      ;; Kickoff report monitor broadcasting
      (a/go-loop []
        (when-let [changes (a/<! (:output-ch tx-report-monitor))]
          (try
            (broadcast chsk [:sheet/tx-data changes])
            (catch Exception e
              (timbre/error e "Broadcast failed")))
          (recur)))

      (assoc (select-keys chsk SENTE_KEYS)
        :stop-fn stop-fn)))
  (stop [this]
    (timbre/info "Stopping Channel Sockets listener...")
    (if-let [stop (:stop-fn this)]
      (stop)
      (timbre/error "No stop-fn found for Channel Sockets listener. Doing nothing."))
    (apply dissoc this SENTE_KEYS :stop-fn)))

(defn component [] (map->ChannelSockets {}))
