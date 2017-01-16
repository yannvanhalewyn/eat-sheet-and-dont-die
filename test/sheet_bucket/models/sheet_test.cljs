(ns sheet-bucket.models.sheet-test
  (:require [sheet-bucket.models.sheet :refer [new-sheet zipper navigate-to]]
            [cljs.test :refer-macros [deftest is]]
            [clojure.zip :refer [node]]))

(deftest navigate-to
  (is (= "1" (-> new-sheet zipper (navigate-to "1") node :id)))
  (is (= nil (-> new-sheet zipper (navigate-to "2")))))
