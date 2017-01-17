(ns sheet-bucket.models.sheet
  (:require [clojure.zip
             :as zip
             :refer [up down right left end? next node insert-right lefts]]))

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

(defn add [bar new-chord]
  (update bar 0 conj new-chord))

(defn add-bar
  "Takes a sheet zipped to the current chord, and adds a bar after the
  current one. Returns the zipper zipped to the first chord of the new
  bar."
  [chord-loc new-chord-id]
  (-> chord-loc up (insert-right (new-bar new-chord-id)) right down))

(defmulti append (fn [_ t _] t))

(defmethod append :chord [chord-loc _ new-chord-id]
  (-> chord-loc (insert-right (new-chord new-chord-id)) right))

(defmethod append :bar [chord-loc _ new-chord-id]
  (-> chord-loc up (insert-right (new-bar new-chord-id)) right down))

(defmethod append :row [chord-loc _ new-chord-id]
  (-> chord-loc up up (insert-right (new-row new-chord-id)) right down down))

(defmethod append :section [chord-loc _ new-chord-id]
  (-> chord-loc up up up (insert-right (new-section new-chord-id)) right down down down))

(defn- nth-child [loc n]
  (nth (iterate right (down loc)) n))

(defn echo [a] (.log js/console a) a)
(defn move [loc direction]
  (case direction
    :right-chord (-> loc right)
    :right (-> loc up right down)
    :left (-> loc up left down)
    :up (-> loc up up left down down)
    :down
    (let [pos (-> loc up lefts count)
          next-row (-> loc up up right)]
      (down (or (nth-child next-row pos)
                (-> next-row down zip/rightmost))))))
