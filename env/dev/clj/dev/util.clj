(ns dev.util
  (:require [clojure.string :as str]))

(defn exec
  "Executes a process in the background, and returns a java process object."
  [& args]
  (.exec (Runtime/getRuntime) (into-array String args)))

(defn expand-home [s]
  (if (.startsWith s "~")
    (str/replace-first s "~" (System/getProperty "user.home"))
    s))
