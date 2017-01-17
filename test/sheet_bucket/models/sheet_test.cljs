(ns sheet-bucket.models.sheet-test
  (:require [sheet-bucket.models.sheet
             :refer [new-sheet zipper navigate-to append]
             :as sheet]
            [cljs.test :refer-macros [deftest is]]
            [goog.string :refer [format]]
            [clojure.zip :refer [node up left down children]]))

(def test-loc (-> new-sheet zipper (navigate-to "1")))

(deftest navigateTo
  (is (= "1" (-> new-sheet zipper (navigate-to "1") node :id)))
  (is (= nil (-> new-sheet zipper (navigate-to "2"))))
  (is (= "1" (-> new-sheet zipper (navigate-to "1") (append :chord "2") (navigate-to "1") node :id))))

(deftest addChord
  (let [new-chord (-> test-loc (append :chord "2"))]
    (is (= 2 (-> new-chord up children count)))
    (is (= "2" (-> new-chord node :id)))))

(deftest addBar
  (let [new-chord (-> test-loc (append :bar "2"))]
    (is (= 2 (-> new-chord up up children count)))
    (is (= "2" (-> new-chord node :id)))))

(deftest addRow
  (let [new-chord (-> test-loc (append :row "2"))]
    (is (= 2 (-> new-chord up up up children count)))
    (is (= "2" (-> new-chord node :id)))))

(deftest addSection
  (let [new-chord (-> test-loc (append :section "2"))]
    (is (= 2 (-> new-chord up up up up children count)))
    (is (= "2" (-> new-chord node :id)))))



(deftest move
  (let [sheet (-> test-loc
                  (append :chord "2") (append :bar "3")
                  (append :row "4") (append :bar "5")
                  (append :row "6")
                  (append :row "7") (append :bar "8") (append :chord "9")
                  (append :section "10")
                  (navigate-to "1"))
        check (fn [moves expected]
                (let [land (reduce
                            #(let [move (if (string? %2) sheet/navigate-to sheet/move)]
                               (move %1 %2))
                            sheet moves)]
                  (is land (str "Couldn't find element for moves: " moves))
                  (is (= expected (-> land node :id))
                      (format "Failed move test for %s. Expected: %s, got: %s"
                              moves expected (-> land node :id)))))]
    ;; Testing the moves in a sheet.
    ;; |-----+-----|
    ;; | 1 2 | 3   |
    ;; | 4   | 5   |
    ;; | 6   |     |
    ;; | 7   | 8 9 |
    ;; |-----+-----|
    ;; | 10  |     |
    ;; Basics
    (check [:right] "2")
    (check [:right :left] "1")
    (check [:left] "1")
    (check [:right :bar-left] "1")

    (check [:bar-right] "3")
    (check [:down :up] "1")
    (check [:bar-right :down] "5")

    ;; Make a little circle for sanity
    (check [:down] "4")
    (check [:down :bar-right] "5")
    (check [:down :bar-right :up] "3")
    (check [:down :bar-right :up :bar-left] "1")

    ;; ;; Wrap arounds
    (check [:down :bar-left] "3")
    (check [:bar-right :bar-right] "4")
    (check [:right :right] "3")
    (check [:right :right :right] "4")
    (check [:bar-right :left] "2")
    (check [:down :left] "3")

    ;; Section wrap arounds
    ;; LEFT RIGHT
    (check ["8" :bar-right] "10")
    (check ["9" :right] "10")
    (check ["10" :left] "9")

    ;; Out of bounds
    (check [:down :down :down :bar-right :bar-right :bar-right] "10")
    (check [:up] "1")
    (check [:bar-left] "1")
    (check [:down :bar-right :down] "6")
    (check [:down :down :down :bar-right :up] "6")))
