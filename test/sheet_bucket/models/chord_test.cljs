(ns sheet-bucket.models.chord-test
  (:require [sheet-bucket.models.chord :refer [parse]]
            [goog.string :refer [format]]
            [cljs.test :refer-macros [deftest is testing]]))

(def roots ["a" "b" "c" "d" "e" "f" "g"
            "A" "B" "C" "D" "E" "F" "G"
            "1" "2" "3" "4" "5" "6" "7"])

(defn check [[root triad seventh] raw]
  (let [result (parse raw)
        run #(is (= %1 (%2 result))
                 (format "%s - expected %s from %s, got: %s"
                         (name %2) %1 raw (%2 result)))]
    (run root :root)
    (run triad :triad)
    (run seventh :seventh)))

(deftest roots-test
  (every? #(check [[%] :major] %) roots)
  (every? #(check [[% :flat] :major] (str "b" %)) roots)
  (every? #(check [[% :flat] :major] (str % "b")) roots)
  (every? #(check [[% :sharp] :major] (str "#" %)) roots)
  (every? #(check [[% :sharp] :major] (str % "#")) roots))

(deftest triads
  (check [["a"] :minor] "am")
  (check [["b"] :minor] "b-")
  (check [["c"] :minor] "cmin")
  (check [["d"] :augmented] "daug")
  (check [["e"] :augmented] "e+")
  (check [["B" :flat] :diminished] "Bbb5")
  (check [["B"] :diminished] "Bb5"))

(deftest sevenths
  (check [["a"] :major :minor] "a7")
  (check [["g"] :minor :minor] "g-7")
  (check [["7"] :major :minor] "77")
  (check [["B"] :major :minor] "B7")
  (check [["C"] :major :major] "Cmaj")
  (check [["A" :flat] :major :major] "Abmaj7")
  (check [["D" :flat] :minor :major] "Dbminmaj7")
  (check [["F" :flat] :minor :major] "FbminMaj7")
  (check [["C"] :minor :major] "C-maj7"))
