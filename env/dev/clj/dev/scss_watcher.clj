(ns dev.scss-watcher
  (:require [dev.background-process :as bp]
            [com.stuartsierra.component :as component]))

(def DEFAULTS
  {:executable-path "scss"
   :input-file "resources/scss/application.scss"
   :output-file "resources/public/css/application.css"})

(defn watcher
  ([] (watcher DEFAULTS))
  ([{cmd :executable-path in :input-file out :output-file}]
   (bp/->BackgroundProcess "SCSS watcher"
     [cmd "-r sass-globbing --watch" (str in ":" out)])))

#_(def w (atom (watcher)))

#_(def args (:args @w))
#_(def p (dev.util/exec ))
