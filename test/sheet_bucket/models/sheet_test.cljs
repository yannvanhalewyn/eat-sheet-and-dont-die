(ns sheet-bucket.models.sheet-test
  (:require [sheet-bucket.models.sheet :refer [new-sheet zipper navigate-to add-bar]]
            [cljs.test :refer-macros [deftest is]]
            [clojure.zip :refer [node up children down]]))

(deftest navigateto
  (is (= "1" (-> new-sheet zipper (navigate-to "1") node :id)))
  (is (= nil (-> new-sheet zipper (navigate-to "2")))))

(deftest addbar
  (let [new-chord (-> new-sheet zipper (navigate-to "1") (add-bar "2"))]
    (is (= 2 (-> new-chord up up children count)))
    (is (= "2" (-> new-chord node :id)))))
