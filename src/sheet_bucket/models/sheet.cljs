(ns sheet-bucket.models.sheet
  (:require [clojure.zip :as zip]))

(defn zipper
  "Builds the sheet zipper"
  [data]
  (zip/zipper vector? first #(assoc %1 0 %2) data))
