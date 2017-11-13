(ns frontend.util.util)

(defmacro defselector
  "Enables easier selector composition, but ultimatly only generates a simple
  function accepting a single argument: the root app-state.

  Example:

    (def app-state {:first-name \"John\" :last-name \"Williams\"})
    (def first-name :first-name)
    (def last-name :last-name)

    (defselector full-name [first-name last-name]
      (str first-name \" \" last-name))

    (full-name app-state)
    ;; => \"John Williams\""
  [name selectors & body]
  `(def ~name
     (fn [state#]
       (let [args# (map #(% state#) ~selectors)]
         (apply (fn ~selectors ~@body) args#)))))
