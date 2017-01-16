(ns sheet-bucket.selectors
  (:require [sheet-bucket.models.sheet :as sheet]
            [redux.utils :refer-macros [defselector]]))

;; Selector
(def sheet-raw :sheet)
(def selected :selected)

(defselector sections [sheet-raw] (first sheet-raw))
(defselector attributes [sheet-raw] (second sheet-raw))

(defselector sheet [sheet-raw] (sheet/zipper sheet-raw))

(defselector current-loc
  [sheet selected]
  (sheet/navigate-to sheet selected))
