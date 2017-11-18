(ns frontend.selectors
  (:require [frontend.models.sheet :as sheet]
            [frontend.util.util :refer-macros [defselector]]))

(def current-user :db/current-user)
(def active-route :db/active-route)
(def params (comp :route/params active-route))

(defn current-sheet-id [db]
  (-> db params :sheet/id js/parseInt))

(defn sheet [db]
  (get-in db [:db/sheets.by-id (current-sheet-id db)]))

(def selection :db/selection)

(defselector sheet-loc [sheet] (sheet/zipper sheet))

(defselector current-loc [sheet-loc selection]
  (sheet/navigate-to sheet-loc (:selection/id selection)))
