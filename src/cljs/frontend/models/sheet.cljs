(ns frontend.models.sheet
  (:require [clojure.zip
             :as zip
             :refer [up down right left end? next node insert-right lefts branch? children]]
            [frontend.util.zipper :refer [nth-child next-leaf prev-leaf locate locate-left]]))

;; Initializing nodes
;; ==================

(def gen-temp-id
  (let [count (atom 0)]
    (fn [] (swap! count dec) (str @count))))

(defn new-chord [id]
  {:db/id id :chord/value ""})

(defn new-bar [[id chord-id]]
  {:db/id id
   :bar/chords [(new-chord chord-id)]})

(defn new-row [[id & ids]]
  {:db/id id
   :row/bars [(new-bar ids)]})

(defn new-section [[id & ids]]
  {:db/id id
   :section/title "Intro"
   :section/rows [(new-row ids)]})

(defn new-sheet [[id & ids]]
  {:db/id id
   :sheet/title "Title"
   :sheet/artist "Artist"
   :sheet/sections [(new-section ids)]})

;; Zipper
;; ======

(def BRANCHING_KEYS #{:sheet/sections :section/rows :row/bars :bar/chords})

(def get-branching-key #(first (filter % BRANCHING_KEYS)))
(def get-children  #(some % BRANCHING_KEYS))

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

(defn replace-temp-ids [sheet replacements]
  (loop [loc (zipper sheet)]
    (if (zip/end? loc)
      (zip/root loc)
      (if-let [new-id (get replacements (:db/id (zip/node loc)))]
        (recur (zip/next (zip/edit loc assoc :db/id new-id)))
        (recur (zip/next loc))))))

;; Adding
;; ======

(defmulti append (fn [_ t _] t))

(defmethod append :chord [chord-loc _ [id]]
  (-> chord-loc (insert-right (new-chord id)) right))

(defmethod append :bar [chord-loc _ ids]
  (-> chord-loc up (insert-right (new-bar ids)) right down))

(defmethod append :row [chord-loc _ ids]
  (-> chord-loc up up (insert-right (new-row ids)) right down down))

(defmethod append :section [chord-loc _ ids]
  (-> chord-loc up up up (insert-right (new-section ids)) right down down down))

;; Removing
;; ========

(def empty-branch? #(and (zip/branch? %) (empty? (zip/children %))))

(defn- nearest-chord
  "Returns the location if chord, the previous chord if any or the next chord"
  [loc]
  (or (if-not (zip/branch? loc) loc)
      (prev-leaf loc)
      (next-leaf loc)))

(defn- remove-and-clear-empty-parents
  [loc]
  (loop [l loc]
    (let [prev (zip/remove l)]
      (if (empty-branch? prev)
        (recur prev)
        (nearest-chord prev)))))

(defmulti delete (fn [_ t] t))

(defmethod delete :chord [loc _]
  (remove-and-clear-empty-parents loc))

(defmethod delete :bar [loc _]
  (remove-and-clear-empty-parents (up loc)))

(defmethod delete :row [loc _]
  (remove-and-clear-empty-parents (-> loc up up)))

(defmethod delete :section [loc _]
  (let [section (-> loc up up up)]
    (if (= 1 (-> section up children count))
      loc
      (remove-and-clear-empty-parents section))))

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

;; Bars / tools
;; ============

(defn- toggle [loc type]
  (zip/edit (up loc) update type not))
