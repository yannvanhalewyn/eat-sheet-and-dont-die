(ns frontend.models.sheet-test
  (:require [frontend.models.sheet
             :refer [zipper navigate-to append delete]
             :as sheet]
            [cljs.test :refer-macros [deftest is testing]]
            [goog.string :refer [format]]
            [clojure.zip :refer [node up left down children rights]]
            [clojure.zip :as zip]))

(def id-pool (repeatedly sheet/gen-temp-id))
(def new-sheet (sheet/new-sheet ["sheet" "section1" "row1" "bar1" "chord1"]))

(def test-loc (-> new-sheet zipper (navigate-to "chord1")))

(deftest navigateTo
  (is (= "chord1" (-> new-sheet zipper (navigate-to "chord1") node :db/id)))
  (is (= nil (-> new-sheet zipper (navigate-to 100))))
  (is (= "chord1" (-> new-sheet zipper (navigate-to "chord1")
                    (append :chord id-pool) (navigate-to "chord1") node :db/id))))

(deftest addChord
  (let [new-chord (-> test-loc (append :chord id-pool))]
    (is (= 2 (-> new-chord up children count)))
    (is (= (first id-pool) (-> new-chord node :db/id)))))

(deftest addBar
  (let [ids (take 2 id-pool)
        new-chord (-> test-loc (append :bar ids))]
    (is (= 2 (-> new-chord up up children count)))
    (is (= (last ids) (-> new-chord node :db/id)))))

(deftest addRow
  (let [ids (take 3 id-pool)
        new-chord (-> test-loc (append :row ids))]
    (is (= 2 (-> new-chord up up up children count)))
    (is (= (last ids) (-> new-chord node :db/id)))))

(deftest addSection
  (let [ids (take 4 id-pool)
        new-chord (-> test-loc (append :section ids))]
    (is (= 2 (-> new-chord up up up up children count)))
    (is (= (last ids) (-> new-chord node :db/id)))))

(deftest removing
  (let [sheet (-> test-loc
                (append :chord ["chord2"])
                (append :bar ["bar2" "chord3"])
                (append :row ["row2" "bar3" "chord4"])
                (append :section ["section2" "row3" "bar4" "chord5"])
                (navigate-to "chord1"))]
    ;; |-----+---|
    ;; | 1 2 | 3 |
    ;; | 4   |   |
    ;; |-----+---|
    ;; | 5   |   |

    ;; Chords
    ;; ======
    (is (= ["chord2"] (-> sheet (delete :chord) up children (#(map :db/id %)))))
    (is (= "chord2" (-> sheet (delete :chord) node :db/id)))
    (is (= "chord1" (-> sheet (navigate-to "chord2") (delete :chord) node :db/id)))
    (is (= "chord2" (-> sheet (navigate-to "chord3") (delete :chord) node :db/id)))
    (is (= "chord3" (-> sheet (navigate-to "chord4") (delete :chord) node :db/id)))
    (is (= "chord4" (-> sheet (navigate-to "chord5") (delete :chord) node :db/id)))
    (is (= 0 (-> sheet (navigate-to "chord3") (delete :chord) up rights count)))
    (is (= 0 (-> sheet (navigate-to "chord4") (delete :chord) up up rights count)))
    (is (= 1 (-> sheet (navigate-to "chord5") (delete :chord) up up up up children count)))

    ;; ;; Bars
    ;; ;; ====
    (is (= "chord3" (-> sheet (delete :bar) node :db/id)))
    (is (= "chord3" (-> sheet (navigate-to "chord4") (delete :bar) node :db/id)))
    (is (= 1 (-> sheet (navigate-to "chord2") (delete :bar) up up children count)))
    (is (= 1 (-> sheet (navigate-to "chord4") (delete :bar) up up up children count)))
    (is (= 1 (-> sheet (navigate-to "chord5") (delete :bar) up up up up children count)))

    ;; ;; Rows
    ;; ;; ====
    (is (= "chord4" (-> sheet (delete :row) node :db/id)))
    (is (= 1 (-> sheet (navigate-to "chord4") (delete :row) up up up children count)))
    (is (= 1 (-> sheet (navigate-to "chord5") (delete :row) up up up up children count)))

    ;; ;; Sections
    ;; ;; ========
    (is (= "chord5" (-> sheet (delete :section) node :db/id)))
    (is (= 1 (-> sheet (delete :section) up up up up children count)))
    (is (= 1 (-> sheet (navigate-to "chord5") (delete :section) up up up up children count)))

    (testing "Removing last section"
      (let [sheet-with-one-section (delete sheet :section)]
        (is (= sheet-with-one-section (delete sheet-with-one-section :section)))))))

(deftest move
  (let [sheet (-> test-loc
                (append :chord ["chord2"]) (append :bar ["bar2" "chord3"])
                (append :row ["row2" "bar3" "chord4"]) (append :bar ["bar4" "chord5"])
                (append :row ["row3" "bar5" "chord6"])
                (append :row ["row4" "bar6" "chord7"]) (append :bar ["bar7" "chord8"])
                (append :bar ["bar8" "chord9"]) (append :chord ["chord10"])
                (append :section ["section2" "row5" "bar9" "chord11"])
                (append :section ["section3" "row6" "bar10" "chord12"]) (append :bar ["bar11" "chord13"])
                (navigate-to "chord1"))
        check (fn [moves expected]
                (let [land (reduce
                             #(let [move (if (string? %2) sheet/navigate-to sheet/move)]
                                (move %1 %2))
                             sheet moves)]
                  (is land (str "Couldn't find element for moves: " moves))
                  (is (= expected (-> land node :db/id))
                    (format "Failed move test for %s. Expected: %s, got: %s"
                      moves expected (-> land node :db/id)))))]

    ;; Testing the moves in a sheet.
    ;; |-----+----+------|
    ;; | 1 2 | 3  |      |
    ;; | 4   | 5  |      |
    ;; | 6   |    |      |
    ;; | 7   | 8  | 9 10 |
    ;; |-----+----+------|
    ;; | 11  |    |      |
    ;; |-----+----+------|
    ;; | 12  | 13 |      |

    ;; Basics
    ;; ======
    (check [:right] "chord2")
    (check [:right :left] "chord1")
    (check [:right :bar-left] "chord1")
    (check [:bar-right] "chord3")
    (check [:down :up] "chord1")
    (check [:bar-right :down] "chord5")
    (check [:bar-right :left] "chord2")
    (check ["chord7" :down] "chord11")
    (check ["chord11" :up] "chord7")

    ;; Make a little circle for sanity
    ;; ===============================
    (check [:down] "chord4")
    (check [:down :bar-right] "chord5")
    (check [:down :bar-right :up] "chord3")
    (check [:down :bar-right :up :bar-left] "chord1")

    ;; Vertical jumping to latest bar
    ;; ==============================
    (check ["chord5" :down] "chord6")
    (check ["chord8" :up] "chord6")
    (check ["chord9" :up] "chord6")
    (check ["chord3" :right] "chord4")
    (check ["chord7" :down] "chord11")
    (check ["chord11" :up] "chord7")

    ;; Wrap arounds
    ;; ============
    (check ["chord4" :left] "chord3")
    (check ["chord4" :bar-left] "chord3")
    (check ["chord3" :bar-right] "chord4")

    ;; Section wrap arounds
    ;; ====================
    (check ["chord9" :bar-right] "chord11")
    (check ["chord10" :right] "chord11")
    (check ["chord11" :left] "chord10")
    (check ["chord11" :bar-left] "chord9")
    (check ["chord9" :down] "chord11")
    (check ["chord13" :up] "chord11")

    ;; Out of bounds
    ;; =============
    (testing "Will return nil when leaving sheet edges"
      (let [is-nil #(is (= nil (-> sheet (sheet/navigate-to %1) (sheet/move %2))))]
        (is-nil "chord13" :right)
        (is-nil "chord13" :bar-right)
        (is-nil "chord1" :left)
        (is-nil "chord1" :bar-left)
        (is-nil "chord1" :up)
        (is-nil "chord12" :down)))))

(deftest bars
  (is (-> (sheet/toggle test-loc :bar/end-repeat)
        zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/end-repeat])))
  (is (-> (sheet/toggle test-loc :bar/start-repeat)
        zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/start-repeat]))))
