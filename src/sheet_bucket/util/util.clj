(ns sheet-bucket.util.util)

(defmacro fori
  "clojure.core/for with indexes. Index is bound to the 0th element in binding array.

   Example:
     (fori [i x [4 5 6]] [x i])
     ;; => [[4 1] [5 2] [6 3]] "
  ([[index-sym val-sym coll] & body]
   `(doall
     (map-indexed
      (fn [~index-sym ~val-sym]
        ~@body)
      ~coll))))
