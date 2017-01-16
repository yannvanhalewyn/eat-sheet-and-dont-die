(ns sheet-bucket.reducer
  (:require [sheet-bucket.models.sheet :refer [new-sheet]]))

(defn app [state action]
  (case (:type action)
    :init {:sheet new-sheet :selected "2"}
    :select-chord (assoc state :selected (:id action))
    :sheet/update (assoc state :sheet (:value action) :selected nil)
    state))
