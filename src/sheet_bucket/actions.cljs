(ns sheet-bucket.actions
  (:require [redux.core :refer [transact!]]
            [clojure.zip :as zip]
            [sheet-bucket.selectors :refer [selected current-loc]]))

(defn select-chord [state id]
  (transact! state {:type :select-chord :id id}))

(defn update-chord [state _ value]
  (transact! state {:type :sheet/update
                    :value (zip/root (zip/edit (current-loc @state)
                                               assoc :raw value))}))
