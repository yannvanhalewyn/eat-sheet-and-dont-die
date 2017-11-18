(ns dev.scss-watcher
  (:import java.io.File)
  (:require [dev.background-process :as bp]
            [com.stuartsierra.component :as c]))

(def DEFAULTS
  {:input-file "scss/application.scss"
   :output-file "resources/public/css/application.css"})

(defn watcher
  ([] (watcher DEFAULTS))
  ([{in :input-file out :output-file}]
   (let [tmp-file (java.io.File/createTempFile "application" ".css")
         tmp-path (.getAbsolutePath tmp-file)]
     (c/system-map
       :scss-watcher (bp/->BackgroundProcess "SCSS watcher"
                       ["sass" "-r sass-globbing --watch" (str in ":" tmp-path)])
       :auto-prefixer (bp/->BackgroundProcess "CSS Auto Prefixer"
                        ["postcss" tmp-path "--use autoprefixer" "--watch" "-o" out])))))
