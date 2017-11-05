(ns dev.scss-watcher
  (:require [com.stuartsierra.component :as component])
  (:import java.lang.Runtime))

(def DEFAULTS
  {:executable-path "scss"
   :input-dir "resources/scss"
   :output-dir "resources/public/css"})

(defrecord ScssWatcher [executable-path input-dir output-dir]
  component/Lifecycle
  (start [component]
    (if-not (:process component)
      (do
        (println "Starting SCSS watch process...")
        (assoc component :process
               (.exec (Runtime/getRuntime)
                      (str executable-path " --watch " input-dir ":" output-dir))))
      component))
  (stop [component]
    (when-let [process (:process component)]
      (println "Stopping SCSS watch process...")
      (.destroy process))
    component))

(defn watcher
  ([] (map->ScssWatcher DEFAULTS))
  ([config] (map->ScssWatcher (merge config DEFAULTS))))
