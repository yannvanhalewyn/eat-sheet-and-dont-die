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

(defn check-move [sheet moves expected]
  (let [land (reduce #(sheet/move %1 %2) sheet moves)]
    (is land (str "Couldn't find element for moves: " moves))
    (is (= expected (-> land node :id))
        (format "Failed move test for %s. Expected: %s, got: %s"
                moves expected (-> land node :id)))))

(deftest move
  ;; [1 2] 3
  ;; 4 5
  ;; 6
  ;; 7 8
  ;; --
  ;; 9
  (let [sheet (-> test-loc
                  (append :chord "2") (append :bar "3")
                  (append :row "4") (append :bar "5")
                  (append :row "6")
                  (append :row "7") (append :bar "8")
                  (append :section "9")
                  (navigate-to "1"))]
    ;; ;; Basics
    (check-move sheet [:chord-right] "2")
    (check-move sheet [:chord-right :chord-right] "2")
    (check-move sheet [:chord-right :chord-left] "1")
    (check-move sheet [:chord-left] "1")

    (check-move sheet [:right] "3")
    (check-move sheet [:down :up] "1")
    (check-move sheet [:right :down] "5")

    ;; ;; Make a little circle for sanity
    (check-move sheet [:down] "4")
    (check-move sheet [:down :right] "5")
    (check-move sheet [:down :right :up] "3")
    (check-move sheet [:down :right :up :left] "1")

    ;; ;; Out of bounds
    (check-move sheet [:right :right] "3")
    (check-move sheet [:up] "1")
    (check-move sheet [:left] "1")
    (check-move sheet [:down :right :down] "6")
    (check-move sheet [:down :down :down :right :up] "6")))
