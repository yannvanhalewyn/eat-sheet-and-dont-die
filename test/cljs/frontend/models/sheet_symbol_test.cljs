(ns frontend.models.sheet-symbol-test
  (:require [frontend.models.sheet-symbol :as sut]
            [frontend.models.sheet :as sheet]
            [clojure.zip :as zip]
            [cljs.test :as t :refer-macros [deftest is]]))

(def sheet-loc (-> (sheet/new-sheet ["sheet" "section1" "row1" "bar1" "chord1"])
                 sheet/zipper
                 (sheet/navigate-to "chord1")))

(deftest add
  (is (-> (sut/add sheet-loc :bar/end-repeat) zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/end-repeat])))
  (is (-> (sut/add sheet-loc :bar/start-repeat) zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/start-repeat])))
  (is (= (:bar/segno sut/defaults)
        (-> (sut/add sheet-loc :bar/segno) zip/root
          (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/segno]))))
  (is (= (:bar/coda sut/defaults)
        (-> (sut/add sheet-loc :bar/coda) zip/root
          (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/coda])))))
