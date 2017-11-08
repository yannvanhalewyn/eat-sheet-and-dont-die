(ns redux.utils
  (:require [reagent.core :as reagent]
            [redux.core :refer [transact!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn map-values
  "Applies function f to each item in the map s and returns a map."
  [f coll]
  (reduce-kv #(assoc %1 %2 (f %3)) {} coll))

(defn create-container
  "Returns a function that creates a reagent class from given
  component. Selectors execute against the state and the results
  also get passed in as props."
  [& {:keys [selectors actions component]}]
  (fn [state]
    (reagent/create-class
     {:reagent-render
      (fn [state children]
        [component
         (merge
          (map-values #(% @state) selectors)
          (map-values #(partial % state) actions))
         children])})))

(defn create-selector
  [selectors f]
  (let [cache (atom [])]
    (fn [state]
      (let [args (map #(% state) selectors)
            [cached-args cached-result] @cache]
        (if (= args cached-args)
          cached-result
          (let [result (apply f args)]
            (reset! cache [args result])
            result))))))
