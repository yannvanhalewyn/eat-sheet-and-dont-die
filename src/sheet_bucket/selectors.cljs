(ns sheet-bucket.selectors
  (:require [sheet-bucket.models.sheet :as sheet]
            [clojure.zip :as zip]
            [redux.utils :refer-macros [defselector]]))

;; Selector
(def sheet :sheet)
(def selected :selected)
(defselector editor-zip [sheet] (sheet/zipper sheet))

(defselector sections [sheet] (first sheet))
(defselector attributes [sheet] (second sheet))

(defselector current-loc
  [editor-zip selected]
  (sheet/navigate-to editor-zip selected))
