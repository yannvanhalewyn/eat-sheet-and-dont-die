(ns sheet-bucket.selectors
  (:require [sheet-bucket.models.chord :refer [parse]]))

;; Selector
(def section-name :name)
(def selected :current)
(def rows-raw :rows)

(defn rows [state]
  (for [row (rows-raw state)]
    (for [bar row]
      (map #(merge % (parse (:raw %))) bar))))
