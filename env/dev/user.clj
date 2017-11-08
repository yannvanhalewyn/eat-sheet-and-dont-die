(ns user
  (:require [sheet-bucket.core :as app]
            [dev.scss-watcher :as scss]
            [figwheel-sidecar.system :as ra-sys]
            [reloaded.repl :as repl :refer [go start stop reset system]]
            [com.stuartsierra.component :as c]))

(def figwheel-config
  (assoc-in (ra-sys/fetch-config) [:data :build-ids] ["dev" "test" "cards"]))

(defn cljs []
  (ra-sys/cljs-repl (:figwheel-system repl/system)))

(defn db-conn [] (:db system))

(defn dev-system
  "Constructs a system map suitable for interactive development."
  []
  (assoc (app/new-system {:port 8080})
    :figwheel-system (ra-sys/figwheel-system figwheel-config)
    :scss-watcher (scss/watcher)
    :css-watcher (ra-sys/css-watcher {:watch-paths ["resources/public/css"]})))

(repl/set-init! dev-system)
