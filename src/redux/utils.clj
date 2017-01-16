(ns redux.utils)

(defmacro defselector
  "Enables easier selector composition, but ultimatly only generates a simple
  function accepting a single argument: the root app-state.

  Example:

    (def app-state {:trips {1 \"trip1\" 2 \"trip2\"})
    ;; => #'user/app-state

    (def trips :trips)
    ;; => #'user/trips

    (defselector first-trip [trips] (first trips))
    ;; => #'user/first-trip

    (first-trip app-state)
    ;; => \"trip1\""
  [name selectors & body]
  `(def ~name
     (redux.utils/create-selector ~selectors (fn ~selectors ~@body))))
