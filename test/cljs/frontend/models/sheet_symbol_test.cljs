(ns frontend.models.sheet-symbol-test
  (:require [frontend.models.sheet-symbol :as sut]
            [frontend.models.sheet :as sheet]
            [clojure.zip :as zip]
            [clojure.set :as set]
            [cljs.test :as t :refer-macros [deftest is]]))

(def sheet-loc (-> (sheet/new-sheet ["sheet" "section1" "row1" "bar1" "chord1"])
                 sheet/zipper
                 (sheet/navigate-to "chord1")))

(defn submap? [x y]
  "Returns true iff x is a submap of y"
  (set/subset? (set x) (set y)))

(deftest add
  (is (-> (sut/add sheet-loc :bar/end-repeat) zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/end-repeat])))
  (is (-> (sut/add sheet-loc :bar/start-repeat) zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/start-repeat])))
  (is (submap? {:attachment/type :symbol/segno}
        (-> (sut/add sheet-loc :bar/segno) zip/node :bar/attachments first)))
  (is (submap? {:attachment/type :symbol/coda}
        (-> (sut/add sheet-loc :bar/coda) zip/node :bar/attachments first))))
