(ns sheet-bucket.models.sheet-test
  (:require [sheet-bucket.models.sheet
             :refer [new-sheet zipper navigate-to append delete]
             :as sheet]
            [cljs.test :refer-macros [deftest is testing]]
            [goog.string :refer [format]]
            [clojure.zip :refer [node up left down children rights]]))

(def test-loc (-> new-sheet zipper (navigate-to "1")))

(deftest navigateTo
  (is (= "1" (-> new-sheet zipper (navigate-to "1") node :chord/id)))
  (is (= nil (-> new-sheet zipper (navigate-to "2"))))
  (is (= "1" (-> new-sheet zipper (navigate-to "1")
                 (append :chord "2") (navigate-to "1") node :chord/id))))

(deftest addChord
  (let [new-chord (-> test-loc (append :chord "2"))]
    (is (= 2 (-> new-chord up children count)))
    (is (= "2" (-> new-chord node :chord/id)))))

(deftest addBar
  (let [new-chord (-> test-loc (append :bar "2"))]
    (is (= 2 (-> new-chord up up children count)))
    (is (= "2" (-> new-chord node :chord/id)))))

(deftest addRow
  (let [new-chord (-> test-loc (append :row "2"))]
    (is (= 2 (-> new-chord up up up children count)))
    (is (= "2" (-> new-chord node :chord/id)))))

(deftest addSection
  (let [new-chord (-> test-loc (append :section "2"))]
    (is (= 2 (-> new-chord up up up up children count)))
    (is (= "2" (-> new-chord node :chord/id)))))

(deftest removing
  (let [sheet (-> test-loc
                  (append :chord "2")
                  (append :bar "3")
                  (append :row "4")
                  (append :section "5")
                  (navigate-to "1"))]
    ;; |-----+---|
    ;; | 1 2 | 3 |
    ;; | 4   |   |
    ;; |-----+---|
    ;; | 5   |   |

    ;; Chords
    ;; ======
    (is (= ["2"] (-> sheet (delete :chord) up children (#(map :chord/id %)))))
    (is (= "2" (-> sheet (delete :chord) node :chord/id)))
    (is (= "1" (-> sheet (navigate-to "2") (delete :chord) node :chord/id)))
    (is (= "2" (-> sheet (navigate-to "3") (delete :chord) node :chord/id)))
    (is (= "3" (-> sheet (navigate-to "4") (delete :chord) node :chord/id)))
    (is (= "4" (-> sheet (navigate-to "5") (delete :chord) node :chord/id)))
    (is (= 0 (-> sheet (navigate-to "3") (delete :chord) up rights count)))
    (is (= 0 (-> sheet (navigate-to "4") (delete :chord) up up rights count)))
    (is (= 1 (-> sheet (navigate-to "5") (delete :chord) up up up up children count)))

    ;; ;; Bars
    ;; ;; ====
    (is (= "3" (-> sheet (delete :bar) node :chord/id)))
    (is (= "3" (-> sheet (navigate-to "4") (delete :bar) node :chord/id)))
    (is (= 1 (-> sheet (navigate-to "2") (delete :bar) up up children count)))
    (is (= 1 (-> sheet (navigate-to "4") (delete :bar) up up up children count)))
    (is (= 1 (-> sheet (navigate-to "5") (delete :bar) up up up up children count)))

    ;; ;; Rows
    ;; ;; ====
    (is (= "4" (-> sheet (delete :row) node :chord/id)))
    (is (= 1 (-> sheet (navigate-to "4") (delete :row) up up up children count)))
    (is (= 1 (-> sheet (navigate-to "5") (delete :row) up up up up children count)))

    ;; ;; Sections
    ;; ;; ========
    (is (= "5" (-> sheet (delete :section) node :chord/id)))
    (is (= 1 (-> sheet (delete :section) up up up up children count)))
    (is (= 1 (-> sheet (navigate-to "5") (delete :section) up up up up children count)))

    (testing "Removing last section"
      (let [sheet-with-one-section (delete sheet :section)]
        (is (= sheet-with-one-section (delete sheet-with-one-section :section)))))))

(deftest move
  (let [sheet (-> test-loc
                  (append :chord "2") (append :bar "3")
                  (append :row "4") (append :bar "5")
                  (append :row "6")
                  (append :row "7") (append :bar "8") (append :bar "9") (append :chord "10")
                  (append :section "11")
                  (append :section "12") (append :bar "13")
                  (navigate-to "1"))
        check (fn [moves expected]
                (let [land (reduce
                             #(let [move (if (string? %2) sheet/navigate-to sheet/move)]
                                (move %1 %2))
                             sheet moves)]
                  (is land (str "Couldn't find element for moves: " moves))
                  (is (= expected (-> land node :chord/id))
                      (format "Failed move test for %s. Expected: %s, got: %s"
                              moves expected (-> land node :chord/id)))))]
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
    (check [:right] "2")
    (check [:right :left] "1")
    (check [:right :bar-left] "1")
    (check [:bar-right] "3")
    (check [:down :up] "1")
    (check [:bar-right :down] "5")
    (check [:bar-right :left] "2")
    (check ["7" :down] "11")
    (check ["11" :up] "7")

    ;; Make a little circle for sanity
    ;; ===============================
    (check [:down] "4")
    (check [:down :bar-right] "5")
    (check [:down :bar-right :up] "3")
    (check [:down :bar-right :up :bar-left] "1")

    ;; Vertical jumping to latest bar
    ;; ==============================
    (check ["5" :down] "6")
    (check ["8" :up] "6")
    (check ["9" :up] "6")
    (check ["3" :right] "4")
    (check ["7" :down] "11")
    (check ["11" :up] "7")

    ;; Wrap arounds
    ;; ============
    (check ["4" :left] "3")
    (check ["4" :bar-left] "3")
    (check ["3" :bar-right] "4")

    ;; Section wrap arounds
    ;; ====================
    (check ["9" :bar-right] "11")
    (check ["10" :right] "11")
    (check ["11" :left] "10")
    (check ["11" :bar-left] "9")
    (check ["9" :down] "11")
    (check ["13" :up] "11")

    ;; Out of bounds
    ;; =============
    (testing "Will return nil when leaving sheet edges"
      (let [is-nil #(is (= nil (-> sheet (sheet/navigate-to %1) (sheet/move %2))))]
        (is-nil "13" :right)
        (is-nil "13" :bar-right)
        (is-nil "1" :left)
        (is-nil "1" :bar-left)
        (is-nil "1" :up)
        (is-nil "12" :down)))))
