(ns frontend.selectors
  (:require [datascript.core :as d]
            [frontend.models.sheet :as sheet]
            [frontend.util.util :refer-macros [defselector]]))

(def current-user :db/current-user)
(def active-route :db/active-route)
(def params (comp :route/params active-route))

(defn current-sheet-id [db]
  (-> db params :sheet/id js/parseInt))

(defn sheet [db]
  (d/pull @(:db/sheets-datascript db)
    sheet/pull-selector
    (current-sheet-id db)))

(def selection :db/selection)

(defselector sheet-loc [sheet] (sheet/zipper sheet))

(defselector current-loc [sheet-loc selection]
  (sheet/navigate-to sheet-loc (:selection/id selection)))
