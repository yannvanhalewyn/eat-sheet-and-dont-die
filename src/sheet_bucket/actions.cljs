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

(def adders
  {:chord sheet/add-chord
   :bar sheet/add-bar
   :row sheet/add-row
   :section sheet/add-section})

(defn add-element [state type]
  (if-let [adder (type adders)]
    (let [new-sheet (adder (current-loc @state) (name (gensym)))]
      (transact! state {:type :sheet/update
                        :value (zip/root new-sheet)
                        :selected (-> new-sheet zip/node :id)}))))
