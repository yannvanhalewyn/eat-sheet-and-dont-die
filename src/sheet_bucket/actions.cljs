(ns sheet-bucket.actions
  (:require [redux.core :refer [transact!]]
            [clojure.zip :as zip]
            [sheet-bucket.selectors :refer [rows selected]]))

(defn select-chord [state id]
  (transact! state {:type :select-chord :id id}))

(defn update-chord [state _ value]
  (let [root (zip/vector-zip (rows @state))]
    (loop [loc root]
      (if (= (:id (zip/node loc)) (selected @state))
        (transact! state {:type :update-rows
                          :value (zip/root (zip/edit loc assoc :raw value))})
        (recur (zip/next loc))))))
