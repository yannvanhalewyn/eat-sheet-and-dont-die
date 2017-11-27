(ns frontend.models.sheet
  (:require [shared.utils :as util]
            [clojure.zip
             :as zip
             :refer [up down right left end? next node insert-right lefts branch? children]]
            [frontend.util.zipper :as uzip :refer [nth-child next-leaf prev-leaf
                                                   locate locate-left]]))

;; Zipper
;; ======

(def BRANCHING_KEYS #{:sheet/sections :section/rows :row/bars :bar/chords})

(def get-branching-key #(first (filter % BRANCHING_KEYS)))
(def get-children  #(sort-by :coll/position (some % BRANCHING_KEYS)))

(defn zipper
  "Builds the sheet zipper"
  [data]
  (zip/zipper get-branching-key
              get-children
              #(assoc %1 (get-branching-key %1) (vec %2)) data))

(defn navigate-to
  "Moves the zipper to the child with given id"
  [sheet id]
  (loop [loc (zipper (zip/root sheet))]
    (cond
      (end? loc) nil
      (= id (:db/id (node loc))) loc
      :else (recur (next loc)))))

(defn nearest-chord
  "Returns the location of the previous chord if any or the next chord"
  [loc]
  (or
    (prev-leaf loc)
    (next-leaf loc)))

;; Movement
;; ========

(def chord? (complement branch?))
(def first-chord-of-bar? #(and (chord? %) (= 0 (count (zip/lefts %)))))

(defn- different-row?
  [loc1 loc2]
  (and (chord? loc1)
    (chord? loc2)
    (not= (-> loc1 up up) (-> loc2 up up))))

(defn- move-vertically
  [loc direction]
  (let [bar-idx (-> loc up lefts count)
        locater (case direction :up locate-left :down locate)]
    (if-let [last-chord-in-target-row (locater loc (partial different-row? loc))]
      (let [row (-> last-chord-in-target-row up up)]
        (down (or (nth-child row bar-idx) (-> row down zip/rightmost)))))))

(defn move [loc direction]
  (case direction
    :right (next-leaf loc)
    :left (prev-leaf loc)
    :bar-right (locate (zip/next loc) first-chord-of-bar?)
    :bar-left (locate-left (zip/prev loc) first-chord-of-bar?)
    :up (move-vertically loc :up)
    :down (move-vertically loc :down)))
