(ns cards.util
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.generators]))

(defn alert
  "Returns a handler that launches an alert box with all supplied arguments"
  [& messages]
  #(js/alert (apply str messages)))

(defn gen
  "Will generate any amount of data based on a spec"
  [spec count]
  (gen/sample (s/gen spec) count))
