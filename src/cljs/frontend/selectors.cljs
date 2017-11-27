(ns frontend.selectors
  (:require [datascript.core :as d]
            [frontend.models.sheet-zip :as sheet-zip]
            [frontend.util.util :refer-macros [defselector]]))

(def current-user :db/current-user)
(def active-route :db/active-route)
(def params (comp :route/params active-route))

(defn current-sheet-id [db]
  (-> db params :sheet/id js/parseInt))

(defn sheet [db]
  (d/pull (:db/sheets-datascript db) '[*] (current-sheet-id db)))

(def selection :db/selection)

(defselector sheet-loc [sheet] (sheet-zip/zipper sheet))

(defselector current-loc [sheet-loc selection]
  (sheet-zip/navigate-to sheet-loc (:selection/id selection)))
