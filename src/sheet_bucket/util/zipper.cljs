(ns sheet-bucket.util.zipper
  (:require [clojure.zip :as zip]))

(defn locate
  "Will depth first search the zipper until a node is found for which
  pred returns true"
  [loc pred]
  (loop [l loc]
    (cond
      (pred l) l
      (zip/end? l) nil
      :else (recur (zip/next l)))))

(defn next-leaf
  "Will navigate the tree from current loc with 'next until a leaf is
  found. Returns nil when out of bounds."
  [loc]
  (loop [l (zip/next loc)]
    (cond
      (zip/end? l) nil
      (zip/branch? l) (recur (zip/next l))
      :else l)))

(defn prev-leaf
  "Will navigate the tree from current loc with 'prev until a leaf is
  found. Returns nil when out of bounds."
  [loc]
  (loop [l (zip/prev loc)]
    (if (and l (zip/branch? l))
      (recur (zip/prev l))
      l)))
