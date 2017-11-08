(ns frontend.events
  (:require [frontend.reducer :as reducer]
            [frontend.selectors :as selectors]
            [re-frame.core :refer [reg-event-db]]
            [clojure.zip :as zip]
            [frontend.models.sheet :as sheet]))

(reg-event-db
  :event/init
  (fn [db]
    (reducer/app {} {:type :init})))

(reg-event-db
  :sheet/deselect
  (fn [db]
    (reducer/app db
                 {:type :sheet/clear-selected})))

(reg-event-db
  :sheet/select-chord
  (fn [db [_ id]]
    (reducer/app db
                 {:type :select-chord :id id})))

(reg-event-db
  :sheet/update-chord
  (fn [db [_ value]]
    (reducer/app db
                 {:type :sheet/update-chord
                  :value (zip/root (zip/edit (selectors/current-loc db)
                                             assoc :chord/value value))})))

(reg-event-db
  :sheet/append
  (fn [db [_ type]]
    (let [new-sheet (sheet/append (selectors/current-loc db) type (random-uuid))]
      (reducer/app db
                   {:type :sheet/update
                    :value (zip/root new-sheet)
                    :selected (-> new-sheet zip/node :chord/id)}))))

(reg-event-db
  :sheet/move
  (fn [db [_ dir]]
    (if-let [new-sheet (sheet/move (selectors/current-loc db) dir)]
      (reducer/app db {:type :sheet/update
                       :value (zip/root new-sheet)
                       :selected (-> new-sheet zip/node :chord/id)})
      db)))

(reg-event-db
  :sheet/remove
  (fn [db [_ element]]
    (let [new-sheet (sheet/delete (selectors/current-loc db) element)]
      (reducer/app db {:type :sheet/update
                       :value (zip/root new-sheet)
                       :selected (-> new-sheet zip/node :chord/id)}))))
