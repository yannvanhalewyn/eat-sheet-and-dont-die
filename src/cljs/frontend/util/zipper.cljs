(ns frontend.util.zipper
  (:require [clojure.zip :as zip]))

(defn locate
  "Will depth first search the zipper until a node is found for which
  pred returns true"
  [loc pred]
  (loop [l loc]
    (cond
      (zip/end? l) nil
      (pred l) l
      :else (recur (zip/next l)))))

(defn locate-left
  "Will depth first search backwards the zipper until a node is found
  for which pred returns true"
  [loc pred]
  (loop [l loc]
    (if (and l (not (pred l)))
      (recur (zip/prev l))
      l)))

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

(defn nth-child
  "Jumps to the nth child of loc. Should take a location of a branch"
  [loc n]
  (nth (iterate zip/right (zip/down loc)) n))

(defn edit-children
  "Updates all children by applying (f node idx args). Returns the
  updated zipper at parent location."
  [loc f & args]
  (loop [idx 0 l (zip/down loc)]
    (let [l (zip/edit l #(apply f % idx args))]
      (if-let [next (zip/right l)]
        (recur (inc idx) next)
        (zip/up l)))))
