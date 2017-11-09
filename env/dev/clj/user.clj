(ns user
  (:require [reloaded.repl :as repl]
            [clojure.tools.namespace.repl :refer [refresh]]))

(defn- switch-to-dev []
  (in-ns 'dev)
  :ok)

(defn dev []
  (let [ret (refresh :after `switch-to-dev)]
    (if (instance? Throwable ret)
      (throw ret)
      ret)))

(defn go
  "Loads all source files, starts the application running in
  development mode, and switches to the 'dev' namespace."
  []
  (let [ret (repl/reset)]
    (if (instance? Throwable ret)
      (throw ret)
      (do
        (switch-to-dev)
        ret))))
