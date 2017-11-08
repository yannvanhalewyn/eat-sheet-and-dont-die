(ns sheet-bucket.selectors
  (:require [sheet-bucket.models.sheet :as sheet]
            [redux.utils :refer-macros [defselector]]))

;; Selector
(def sheet :sheet)
(def selected :selected)

(defselector sheet-loc [sheet] (sheet/zipper sheet))
(defselector current-loc [sheet-loc selected]
  (sheet/navigate-to sheet-loc selected))
