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
  (let [sheet (-> test-loc
                  (append :chord "2")
                  (append :bar "3")
                  (append :row "4")
                  (append :bar "5")
                  (append :row "6")
                  (append :section "7")
                  (navigate-to "1"))]
    ;; Basics
    (check-move sheet "3" ) (is (= "3" (-> sheet [:right] node :id)))
    (check-move sheet "2" ) (is (= "2" (-> sheet [:right-chord] node :id)))
    (check-move sheet "1" ) (is (= "1" (-> sheet [:right :left] node :id)))
    (is (= "4" (-> sheet [:down] node :id)))
    (is (= "1" (-> sheet [:down :up] node :id)))
    (is (= "5" (-> sheet [:right :down] node :id)))
    #_(is (= "6" (-> sheet (sheet/move :down) (sheet/move :down) node :id)))

    ;; Out of bounds
    (is (= "3" (-> sheet [:right :right] node :id)))
    (is (= "6" (-> sheet [:down :right :down] node :id)))))
