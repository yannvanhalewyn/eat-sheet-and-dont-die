(ns sheet-bucket.actions
  (:require [redux.core :refer [transact!]]
            [clojure.zip :as zip]
            [sheet-bucket.models.sheet :as sheet]
            [sheet-bucket.selectors :refer [selected current-loc]]))

(defn select-chord [state id]
  (transact! state {:type :select-chord :id id}))

(defn clear-selected [state]
  (transact! state {:type :sheet/clear-selected}))

(defn update-chord [state _ value]
  (transact! state {:type :sheet/update-chord
                    :value (zip/root (zip/edit (current-loc @state)
                                               assoc :raw value))}))

(defn add-bar [state]
  (let [new-sheet (sheet/add-bar (current-loc @state) (name (gensym)))]
    (transact! state {:type :sheet/add-bar
                      :value (zip/root new-sheet)
                      :selected (-> new-sheet zip/node :id)})))
