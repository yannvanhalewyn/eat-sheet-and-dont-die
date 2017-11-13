(ns dev
  (:require [sheet-bucket.config :refer [config]]
            [datomic.api :as d]
            [dev.scss-watcher :as scss]
            [dev.datomic-server :as datomic-server]
            [figwheel-sidecar.system :as ra-sys]
            [reloaded.repl :as repl :refer [go start stop reset system]]
            [sheet-bucket.core :as app]))

(def figwheel-config
  (assoc-in (ra-sys/fetch-config) [:data :build-ids] ["dev" "test" "cards"]))

(defn cljs
  ([] (cljs "dev"))
  ([build-id]
   (ra-sys/cljs-repl (:figwheel-system repl/system) build-id)))

(def db-conn #(get-in system [:db :conn]))
(def db (comp d/db db-conn))

(defn dev-system
  "Constructs a system map suitable for interactive development."
  []
  (assoc (app/new-system {:port 8080 :db-url (:db-url config)})
    :figwheel-system (ra-sys/figwheel-system figwheel-config)
    :scss-watcher (scss/watcher)
    :css-watcher (ra-sys/css-watcher {:watch-paths ["resources/public/css"]})))

(repl/set-init! dev-system)
