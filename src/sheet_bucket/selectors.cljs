(ns sheet-bucket.selectors
  (:require [sheet-bucket.models.chord :refer [parse]]))

;; Selector
(def sheet :sheet)
(def selected :selected)

(def sections #(-> % sheet first))
(def attrs #(-> % sheet second))
(def artist #(:name (attrs %)))
(def title #(:name (attrs %)))
