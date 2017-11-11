(ns frontend.util.zipper-test
  (:require [frontend.util.zipper :as sut :refer [next-leaf prev-leaf locate
                                                  locate-left nth-child]]
            [clojure.zip :as zip :refer [vector-zip node up down right rightmost branch?]]
            [cljs.test :refer-macros [deftest is testing]]))

;; SUBJECT
;; =======
;;                  +
;;                  |
;;    +-------------+------------+
;;    |             |            |
;; +--+--+      +---+---+    +---+---+
;; v     v      |       v    v   v   v
;; 1     2   +--+--+    5    6   7   8
;;           v     v
;;           3     4
;;
(def subject (vector-zip [[1 2] [[3 4] 5] [6 7 8]]))

(defn goto [loc x] (locate loc #(= x (node %))))

(deftest testLocate
  (is (= 3 (node (locate subject #(= 3 (node %))))))
  (is (= [3 4] (node (locate subject #(and (vector? (node %)) (= 4 (last (node %))))))))
  (is (= nil (locate subject (constantly false))))
  (testing "You can use zip/branch? in the predicate"
    (is (= nil (locate (goto subject 8) #(zip/branch? %))))))

(deftest testLocateLeft
  (is (= 3 (node (locate-left (goto subject 4) #(= 3 (node %))))))
  (is (= 5 (node (locate-left (goto subject 7)
                              #(and (not (branch? %)) (not= (up %) (up (goto subject 7))))))))
  (is (= nil (locate-left (goto subject 1) (constantly false))))
  (testing "It can find the same node again"
    (is (= 3 (node (locate-left (goto subject 3) #(= 0 (count (zip/lefts %)))))))))

(deftest nextLeaf
  (is (= 1 (node (next-leaf subject))))
  (is (= 3 (-> subject (goto 2) next-leaf node)))
  (is (= 6 (-> subject (goto 5) next-leaf node)))
  (is (= 8 (-> subject (goto 7) next-leaf node)))
  (is (= nil (-> subject (goto 8) next-leaf))))

(deftest prevLeaf
  (is (= 7 (-> subject (goto 8) prev-leaf node)))
  (is (= 5 (-> subject (goto 6) prev-leaf node)))
  (is (= 2 (-> subject (goto 3) prev-leaf node)))
  (is (= nil (-> subject prev-leaf)))
  (is (= nil (-> subject (goto 1) prev-leaf))))

(deftest nthChild
  (is (= 8 (-> subject down rightmost (nth-child 2) node)))
  (is (= nil (-> subject down rightmost (nth-child 10)))))

(deftest edit-children
  (testing "It hands the child's index to the update fn"
    (is (= [[2 0] [3 1]] (-> subject down (sut/edit-children #(vector (inc %1) %2)) node)))))
