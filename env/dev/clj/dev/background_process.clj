(ns dev.background-process
  (:require [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [com.stuartsierra.component :as c]))

(defn exec
  "Executes a process in the background, and returns a java process object."
  [args]
  (.exec (Runtime/getRuntime) (str/join " " args)))

(defrecord BackgroundProcess [name args]
  c/Lifecycle
  (start [component]
    (if-not (:process component)
      (do
        (timbre/infof "Starting %s..." name)
        (assoc component
          :process (exec args)))
      component))
  (stop [component]
    (when-let [process (:process component)]
      (timbre/infof "Stopping %s..." name)
      (.destroy process))
    component))
