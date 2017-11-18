(ns shared.utils
  (:require [clojure.walk :refer [postwalk]]))

(defn key-by
  "Returns a map of the elements of coll keyed by the value of
  applying f to each element in coll.

  @example
  (def users [{:name \"John\" :id 2} {:name \"Jeff\" :id 3}])
  (key-by :id users) ;; => {2 {:name \"John\" :id 2} 3 {:name \"Jeff\" :id 3}}"
  [f coll]
  (into {} (for [r coll] [(f r) r])))

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

(def gen-temp-id
  (let [count (atom 0)]
    (fn [] (swap! count dec) (str @count))))

(defn replace-temp-ids
  "Takes a structure and a replacements map, and replaces and :db/id
  that has a key in the replacements map with it's value."
  [coll replacements]
  (postwalk
    #(if-let [new-id (get replacements (:db/id %))]
       (assoc % :db/id new-id)
       %)
    coll))

(defn dissoc-in
  "Dissoc's the element at path in coll"
  [coll path]
  (update-in coll (butlast path) dissoc (last path)))

(defn delete-by-id
  "Finds any element with given :db/id and removes it from the tree"
  [tree id]
  (postwalk
    (fn [node]
      (if (and (sequential? node) (map? (first node)))
        (remove #(= id (:db/id %)) node)
        node))
    tree))

(def presence #(if (empty? %) nil %))
