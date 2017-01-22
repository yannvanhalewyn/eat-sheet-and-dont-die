(ns sheet-bucket.models.sheet
  (:require [clojure.zip
             :as zip
             :refer [up down right left end? next node insert-right lefts branch?]]
            [sheet-bucket.util.zipper :refer [nth-child next-leaf prev-leaf locate locate-left]]))

(defn new-chord [id] {:id id :raw ""})
(defn new-bar [chord-id] [[(new-chord chord-id)]])
(defn new-row [chord-id] [[(new-bar chord-id)]])
(defn new-section [chord-id] [[(new-row chord-id)] {:name "Intro"}])

(def new-sheet [[(new-section "1")] {:artist "Artist" :title "Song name"}])

(defn zipper
  "Builds the sheet zipper"
  [data]
  (zip/zipper vector? first #(assoc %1 0 %2) data))

(defn navigate-to
  "Moves the zipper to the child with given id"
  [sheet id]
  (loop [loc (zipper (zip/root sheet))]
    (cond
      (end? loc) nil
      (= id (:id (node loc))) loc
      :else (recur (next loc)))))

(defmulti append (fn [_ t _] t))

(defmethod append :chord [chord-loc _ new-chord-id]
  (-> chord-loc (insert-right (new-chord new-chord-id)) right))

(defmethod append :bar [chord-loc _ new-chord-id]
  (-> chord-loc up (insert-right (new-bar new-chord-id)) right down))

(defmethod append :row [chord-loc _ new-chord-id]
  (-> chord-loc up up (insert-right (new-row new-chord-id)) right down down))

(defmethod append :section [chord-loc _ new-chord-id]
  (-> chord-loc up up up (insert-right (new-section new-chord-id)) right down down down))

(defmulti delete (fn [_ t] t))

(def empty-branch? #(and (zip/branch? %) (empty? (zip/children %))))

(defn- nearest-chord
  "Returns the location if chord, the previous chord if any or the next chord"
  [loc]
  (or (if-not (zip/branch? loc) loc)
      (prev-leaf loc)
      (next-leaf loc)))

(defmethod delete :chord [z _]
  (loop [loc z]
    (let [prev (zip/remove loc)]
      (if (empty-branch? prev)
        (recur prev)
        (nearest-chord prev)))))

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
