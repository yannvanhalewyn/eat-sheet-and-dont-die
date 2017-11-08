(ns frontend.actions
  (:require [redux.core :refer [transact!]]
            [clojure.zip :as zip]
            [frontend.models.sheet :as sheet]
            [frontend.selectors :refer [selected current-loc]]))

(defn select-chord [state id]
  (transact! state {:type :select-chord :id id}))

(defn deselect [state]
  (transact! state {:type :sheet/clear-selected}))

(defn update-chord [state value]
  (transact! state {:type :sheet/update-chord
                    :value (zip/root (zip/edit (current-loc @state)
                                               assoc :chord/value value))}))

(defn append [state type]
  (let [new-sheet (sheet/append (current-loc @state) type (random-uuid))]
    (transact! state {:type :sheet/update
                      :value (zip/root new-sheet)
                      :selected (-> new-sheet zip/node :chord/id)})))

(defn move [state direction]
  (if-let [new-sheet (sheet/move (current-loc @state) direction)]
    (transact! state {:type :sheet/update
                      :value (zip/root new-sheet)
                      :selected (-> new-sheet zip/node :chord/id)})
    (if (= direction :right) (append state :bar))))

(defn delete [state element]
  (let [new-sheet (sheet/delete (current-loc @state) element)]
    (transact! state {:type :sheet/update
                      :value (zip/root new-sheet)
                      :selected (-> new-sheet zip/node :chord/id)})))
