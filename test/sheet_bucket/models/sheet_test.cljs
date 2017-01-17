(ns sheet-bucket.models.sheet-test
  (:require [sheet-bucket.models.sheet :refer [new-sheet zipper navigate-to add-bar add-row add-chord add-section]]
            [cljs.test :refer-macros [deftest is]]
            [clojure.zip :refer [node up left down children]]))

(deftest navigateTo
  (is (= "1" (-> new-sheet zipper (navigate-to "1") node :id)))
  (is (= nil (-> new-sheet zipper (navigate-to "2")))))

(deftest addChord
  (let [new-chord (-> new-sheet zipper (navigate-to "1") (add-chord "2"))]
    (is (= 2 (-> new-chord up children count)))
    (is (= "2" (-> new-chord node :id)))))

(deftest addBar
  (let [new-chord (-> new-sheet zipper (navigate-to "1") (add-bar "2"))]
    (is (= 2 (-> new-chord up up children count)))
    (is (= "2" (-> new-chord node :id)))))

(deftest addRow
  (let [new-chord (-> new-sheet zipper (navigate-to "1") (add-row "2"))]
    (is (= 2 (-> new-chord up up up children count)))
    (is (= "2" (-> new-chord node :id)))))

(deftest addSection
  (let [new-chord (-> new-sheet zipper (navigate-to "1") (add-section "2"))]
    (is (= 2 (-> new-chord up up up up children count)))
    (is (= "2" (-> new-chord node :id)))))
