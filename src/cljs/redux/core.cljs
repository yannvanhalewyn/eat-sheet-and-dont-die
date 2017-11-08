(ns redux.core
  (:require [cljs.reader :as reader]))

(defonce ^:private transact-fn
  (atom nil))

(defn transact!
  "Apply the action on the state, uses the transact function set by register."
  [state action]
  (@transact-fn state action))

(defn apply-middleware
  "Builds a transact function. The reducer is used to create a base transact
  fn which accepts the state and an action. The middleware is wrapped around
  the transact for extra functionality."
  [reducer middleware]
  (let [base-dispatch (fn [state action] (swap! state reducer action))
        handlers (cons base-dispatch middleware)]
    (reduce #(%2 %1) handlers)))

(defn start
  "Combine the reducer and middleware to create a transact! function.
  Initializes the app state by dispatching the :init action."
  [app-state reducer middleware]
  (reset! transact-fn (apply-middleware reducer middleware))
  (transact! app-state {:type :init}))
