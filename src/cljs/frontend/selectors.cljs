(ns frontend.selectors
  (:require [datascript.core :as d]
            [frontend.models.sheet :as sheet]
            [frontend.models.sheet-zip :as sheet-zip]
            [frontend.util.util :refer-macros [defselector]]))

(def current-user :db/current-user)
(def active-route :db/active-route)
(def params (comp :route/params active-route))

(defn current-sheet-id [db]
  (-> db params :sheet/id js/parseInt))

(defn sheet [db]
  (d/pull (:db/sheets db) '[*] (current-sheet-id db)))

(defn sheets-db [db] (:db/sheets db))

(def sheets (comp sheet/pull-all sheets-db))
(def selection :db/selection)

(defselector current-chord-id [selection]
  (when (= (:selection/type selection) :selection/chord)
    (:selection/id selection)))

(defselector current-bar [current-chord-id sheets-db]
  (d/q '[:find (pull ?bar [*]) . :in $ ?chord :where [?bar :bar/chords ?chord]]
    sheets-db current-chord-id))

(defselector sheet-loc [sheet] (sheet-zip/zipper sheet))

(defselector current-loc [sheet-loc selection]
  (sheet-zip/navigate-to sheet-loc (:selection/id selection)))
