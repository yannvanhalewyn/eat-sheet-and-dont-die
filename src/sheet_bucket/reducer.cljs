(ns sheet-bucket.reducer
  (:require [sheet-bucket.models.sheet :refer [new-sheet]]))

(defn app [state action]
  (case (:type action)
    :init {:sheet new-sheet :selected "1"}
    :select-chord (assoc state :selected (:id action))
    :sheet/update-chord (assoc state :sheet (:value action))
    :sheet/add-bar (assoc state :sheet (:value action) :selected (:selected action))
    :sheet/add-chord (assoc state :sheet (:value action) :selected (:selected action))
    :sheet/add-row (assoc state :sheet (:value action) :selected (:selected action))
    :sheet/clear-selected (assoc state :selected nil)
    state))
