(ns user
  (:require [figwheel-sidecar.system :as ra-sys]
            [reloaded.repl :as repl :refer [go start stop reset]]
            [com.stuartsierra.component :as c]))

(def figwheel-config
  (assoc (ra-sys/fetch-config) :build-ids ["dev" "test" "cards"]))

(defn cljs []
  (ra-sys/cljs-repl (:figwheel-system repl/system)))

(defn dev-system
  "Constructs a system map suitable for interactive development."
  []
  (c/system-map
    :figwheel-system (ra-sys/figwheel-system figwheel-config)))

(repl/set-init! dev-system)
