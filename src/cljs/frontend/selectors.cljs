(ns frontend.selectors
  (:require [frontend.models.sheet :as sheet]
            [redux.utils :refer-macros [defselector]]))

(defn current-sheet-id [db]
  (js/parseInt (get-in db [:db/active-route :route/params :sheet/id])))

(defn sheet [db]
  (get-in db [:db/sheets.by-id (current-sheet-id db)]))

(def selected :db/selected)

(defselector sheet-loc [sheet] (sheet/zipper sheet))
(defselector current-loc [sheet-loc selected]
  (sheet/navigate-to sheet-loc selected))
