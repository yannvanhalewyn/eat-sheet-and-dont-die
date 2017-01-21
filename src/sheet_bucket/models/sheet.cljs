(ns sheet-bucket.models.sheet
  (:require [clojure.zip
             :as zip
             :refer [up down right left end? next node insert-right lefts]]
            [sheet-bucket.util.zipper :refer [nth-child next-leaf prev-leaf]]))

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

(def invalid? #(and (zip/branch? %) (empty? (zip/children %))))

(defmethod delete :chord [z _]
  (loop [loc z]
    (let [prev (zip/remove loc)]
      (if (invalid? prev)
        (recur prev)
        (or (if-not (zip/branch? prev) prev) (prev-leaf prev) (next-leaf prev))))))

;; Movement
;; ========
(defn- next-row [loc]
  (if-let [next-row (-> loc up up right)]
    (-> next-row down down)
    (if-let [next-section (-> loc up up up right)]
      (-> next-section down down down)
      loc)))

(defn- next-bar [loc]
  (if-let [next-bar (-> loc up right)]
    (-> next-bar down)
    (next-row loc)))

(defn- prev-row [loc]
  (if-let [prev-row (-> loc up up left)]
    (-> prev-row down zip/rightmost down zip/rightmost)
    (if-let [prev-section (-> loc up up up left)]
      (-> prev-section down zip/rightmost down zip/rightmost down zip/rightmost)
      loc)))

(defn- prev-bar [loc]
  (if-let [prev-bar (-> loc up left)]
    (-> prev-bar down zip/rightmost)
    (prev-row loc)))

(defn move [loc direction]
  (case direction
    :right (or (right loc) (next-bar loc) loc)
    :left (or (left loc) (prev-bar loc) loc)
    :bar-right (next-bar loc)
    :bar-left (-> (prev-bar loc) zip/leftmost)

    ;; Don't worry, this will be refactored soon
    :up (let [pos (-> loc up lefts count)]
          (if-let [up-row (-> loc up up left)]
            (down (or (nth-child up-row pos)
                      (-> up-row down zip/rightmost)))
            (if-let [prev-section (-> loc up up up left)]
              (-> prev-section down zip/rightmost
                  (#(or (nth-child % pos)
                        (zip/rightmost (zip/down %))))
                  down)
              loc)))

    :down (let [pos (-> loc up lefts count)]
            (if-let [next-row (-> loc up up right)]
              (down (or (nth-child next-row pos)
                        (-> next-row down zip/rightmost)))
              (if-let [next-section (-> loc up up up right)]
                (-> next-section
                    down
                    (#(or (nth-child % pos)
                          (zip/rightmost (down (zip/rightmost %)))))
                    down)
                loc)))))
