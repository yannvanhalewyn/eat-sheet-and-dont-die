(ns sheet-bucket.util.util
  (:require [cljs.spec :as s]
            [cljs.spec.impl.gen :as gen]
            [clojure.test.check.generators]))

(defn gen
  "Will generate any amount of data based on a spec"
  [spec count]
  (gen/sample (s/gen spec) count))
