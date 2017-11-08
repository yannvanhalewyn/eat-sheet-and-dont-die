(ns frontend.selectors
  (:require [frontend.models.sheet :as sheet]
            [redux.utils :refer-macros [defselector]]))

;; Selector
(def sheet :db/sheet)
(def selected :db/selected)

(defselector sheet-loc [sheet] (sheet/zipper sheet))
(defselector current-loc [sheet-loc selected]
  (sheet/navigate-to sheet-loc selected))
