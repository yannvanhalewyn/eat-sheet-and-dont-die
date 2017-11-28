(ns sheet-bucket.components.tx-report-monitor
  (:require [shared.datsync :as datsync]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as a]
            [datomic.api :as d]
            [taoensso.timbre :as timbre]))

(defrecord TxReportMonitor [output-ch db]
  component/Lifecycle
  (start [this]
    (timbre/info "Starting TxReportMonitor")
    (let [active (atom true)
          tx-report-queue (d/tx-report-queue (:conn db))]
      ;; Run the monitor on a seperate thread.
      (future (while @active
                (let [report (.take tx-report-queue)
                      changes (datsync/report->datom-vecs report #{:attachment/type})]
                  (a/put! output-ch changes))))
      (assoc this :active active)))
  (stop [this]
    (timbre/info "Stopping TxReportMonitor")
    (reset! (:active this) false)
    this))

(defn component []
  (let [resp-chan (a/chan)]
    (map->TxReportMonitor {:output-ch resp-chan})))
