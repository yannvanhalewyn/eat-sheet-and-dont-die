(ns redux.middleware
  (:require [clojure.data :refer [diff]]))

(defn wrap-logger [next]
  (fn [state action]
    (if goog.DEBUG
      (let [prev-state @state
            group-name (str "Action: " (:type action))
            next-state (next state action)]
        (.groupCollapsed js/console group-name)
        (.info js/console "%c prev state" "color: #9E9E9E; font-weight: bold" (sort prev-state))
        (.info js/console "%c action" "color: #03A9F4; font-weight: bold" action)
        (.info js/console "%c next state" "color: #4CAF50; font-weight: bold" (sort next-state))
        (.info js/console "%c diff" "color: #234565; font-weight: bold" (second (diff prev-state next-state)))
        (.groupEnd js/console group-name))
      (next state action))))
