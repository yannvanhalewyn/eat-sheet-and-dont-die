(ns shared.utils)

(defn pad
  "Adds val padding to any collection until it reaches length n. Will
  always return a coll of length n, even when input exceeds n."
  [n coll val]
  (take n (concat coll (repeat val))))

(defn mappad
  "Like map but if collections aren't all of the same size, the
  smaller ones are padded with the given default value."
  [default f & colls]
  (let [maxlen (apply max (map count colls))]
    (apply map f (map #(pad maxlen % default) colls))))

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
