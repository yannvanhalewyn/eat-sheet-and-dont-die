(ns frontend.models.sheet-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [clojure.zip :refer [children down left node rights up]]
            [datascript.core :as d]
            [frontend.models.sheet :as sheet
             :refer [append delete navigate-to zipper]]
            [frontend.models.sheet-2 :as sut]
            [goog.string :refer [format]]
            [shared.utils :as sutils]
            [clojure.zip :as zip]))

(def id-pool (repeatedly sutils/gen-temp-id))
(def new-sheet (sheet/new-sheet ["sheet" "section1" "row1" "bar1" 5]))

(def test-loc (-> new-sheet zipper (navigate-to 5)))

(deftest navigateTo
  (is (= 5 (-> new-sheet zipper (navigate-to 5) node :db/id)))
  (is (= nil (-> new-sheet zipper (navigate-to 100))))
  (is (= 5 (-> new-sheet zipper (navigate-to 5)
             (append :chord id-pool) (navigate-to 5) node :db/id))))

(deftest addChord
  (let [[id1 id2] (take 2 id-pool)
        new-chord (-> test-loc (append :chord [id1]) left (append :chord [id2]))]
    (testing  "It correctly assigns the chord positions and returns the zipper at the new chord"
      (is (= [0 1 2] (map :coll/position (-> new-chord up children))))
      (is (= id2 (-> new-chord node :db/id))))))

(deftest addBar
  (let [ids (take 4 id-pool)
        ids1 (take 2 ids)
        ids2 (drop 2 ids)
        new-chord (-> test-loc (append :bar ids1) up left down (append :bar ids2))]
    (testing "It correctly assigns the bar positions and returns the zipper at the new chord"
      (is (= [0 1 2] (map :coll/position (-> new-chord up up children))))
      (is (= (last ids) (-> new-chord node :db/id))))))

(deftest addRow
  (let [[ids1 ids2] (partition 3 (take 6 id-pool))
        new-chord (-> test-loc (append :row ids1) up up left down down (append :row ids2))]
    (testing "It correctly assigns the row positions and returns the zipper at the new chord"
      (is (= [0 1 2] (map :coll/position (-> new-chord up up up children))))
      (is (= (last ids2) (-> new-chord node :db/id))))))

(deftest addSection
  (let [[ids1 ids2] (partition 4 (take 8 id-pool))
        new-chord (-> test-loc (append :section ids1) up up up left down down down
                    (append :section ids2))]
    (testing  "It correctly assigns the section positions and returns the zipper at the new chord"
      (is (= [0 1 2]  (map :coll/position (-> new-chord up up up up children))))
      (is (= (last ids2) (-> new-chord node :db/id))))))

(deftest removing
  (let [sheet (-> test-loc
                (append :chord ["chord2"])
                (append :bar ["bar2" "chord3"])
                (append :row ["row2" "bar3" "chord4"])
                (append :section ["section2" "row3" "bar4" "chord5"])
                (navigate-to 5))]
    ;; |-----+---|
    ;; | 1 2 | 3 |
    ;; | 4   |   |
    ;; |-----+---|
    ;; | 5   |   |

    ;; Chords
    ;; ======
    (is (= ["chord2"] (-> sheet (delete :chord) up children (#(map :db/id %)))))
    (is (= "chord2" (-> sheet (delete :chord) node :db/id)))
    (is (= 5 (-> sheet (navigate-to "chord2") (delete :chord) node :db/id)))
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

(def BLANK_SHEET
  {:db/id 1
   :sheet/sections {:db/id 2
                    :coll/position 0
                    :section/rows {:db/id 3
                                   :coll/position 0
                                   :row/bars {:db/id 4
                                              :coll/position 0
                                              :bar/chords {:db/id 5
                                                           :coll/position 0
                                                           :chord/value ""}}}}})

(def db (let [conn (d/create-conn sut/schema)]
          (d/transact! conn [BLANK_SHEET])
          @conn))

(defn- tx-apply [db tx-fn & args]
  (:db-after (d/with db (binding [sut/*string-tmp-ids* false]
                          (apply tx-fn db args)))))
(deftest move
  (let [sheet (-> db
                (tx-apply sut/append :chord 5) (tx-apply sut/append :bar 5)
                (tx-apply sut/append :row 5) (tx-apply sut/append :bar 11)
                (tx-apply sut/append :row 11)
                (tx-apply sut/append :row 16) (tx-apply sut/append :bar 19)
                (tx-apply sut/append :bar 21) (tx-apply sut/append :chord 23)
                (tx-apply sut/append :section 19)
                (tx-apply sut/append :section 28) (tx-apply sut/append :bar 32)
                (d/pull '[*] 1)
                sheet/zipper (navigate-to 5))
        check (fn [moves expected]
                (let [land (reduce
                             #(let [move (if (number? %2) sheet/navigate-to sheet/move)]
                                (move %1 %2))
                             sheet moves)]
                  (is land (str "Couldn't find element for moves: " moves))
                  (is (= expected (-> land node :db/id))
                    (format "Failed move test for %s. Expected: %s, got: %s"
                      moves expected (-> land node :db/id)))))]

    ;; Testing the moves in a sheet.
    ;; |------+-----+-------|
    ;; | 5  6 | 8   |       |
    ;; | 11   | 13  |       |
    ;; | 16   |     |       |
    ;; | 19   | 21  | 23 24 |
    ;; |------+-----+-------|
    ;; | 28   |     |       |
    ;; |------+-----+-------|
    ;; | 32   | 34  |       |

    ;; Basics
    ;; ======
    (check [:right] 6)
    (check [:right :left] 5)
    (check [:right :bar-left] 5)
    (check [:bar-right] 8)
    (check [:down :up] 5)
    (check [:bar-right :down] 13)
    (check [:bar-right :left] 6)
    (check [19 :down] 28)
    (check [28 :up] 19)

    ;; Make a little circle for sanity
    ;; ===============================
    (check [:down] 11)
    (check [:down :bar-right] 13)
    (check [:down :bar-right :up] 8)
    (check [:down :bar-right :up :bar-left] 5)

    ;; Vertical jumping to latest bar
    ;; ==============================
    (check [13 :down] 16)
    (check [21 :up] 16)
    (check [23 :up] 16)
    (check [8 :right] 11)
    (check [19 :down] 28)
    (check [28 :up] 19)

    ;; Wrap arounds
    ;; ============
    (check [11 :left] 8)
    (check [11 :bar-left] 8)
    (check [8 :bar-right] 11)

    ;; Section wrap arounds
    ;; ====================
    (check [23 :bar-right] 28)
    (check [24 :right] 28)
    (check [28 :left] 24)
    (check [28 :bar-left] 23)
    (check [23 :down] 28)
    (check [34 :up] 28)

    ;; Out of bounds
    ;; =============
    (testing "Will return nil when leaving sheet edges"
      (let [is-nil #(is (= nil (-> sheet (sheet/navigate-to %1) (sheet/move %2))))]
        (is-nil 34 :right)
        (is-nil 34 :bar-right)
        (is-nil 5 :left)
        (is-nil 5 :bar-left)
        (is-nil 5 :up)
        (is-nil 32 :down)))))
