(ns sheet-bucket.models.chord-test
  (:require [sheet-bucket.models.chord :refer [parse]]
            [goog.string :refer [format]]
            [cljs.test :refer-macros [deftest is testing]]))

(def roots ["a" "b" "c" "d" "e" "f" "g"
            "A" "B" "C" "D" "E" "F" "G"
            "1" "2" "3" "4" "5" "6" "7"])

(defn check [[root triad seventh ninth] raw]
  (let [result (parse raw)
        run #(is (= %1 (%2 result))
                 (format "%s - expected %s from %s, got: %s"
                         (name %2) %1 raw (%2 result)))]
    (run root :root)
    (run (or triad :major) :triad)
    (run seventh :seventh)
    (run ninth :ninth)))

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
  (check [["e"] :augmented] "e#5")
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
  (check [["C"] :minor :major] "C-maj7")
  (check [["F" :sharp] :diminished :minor] "F#m7b5")
  (check [["F" :sharp] :augmented :minor] "F#m7#5"))

(deftest ninths
  (check [["B"] :major :minor :natural] "B9")
  (check [["C"] :minor :major :natural] "C-maj9")
  (check [["D"] :major :minor :sharp] "D7#9")
  (check [["D"] :major :minor :flat] "D7b9")
  (check [["F" :sharp] :augmented :minor :flat] "F#m7b9#5")
  (check [["F" :sharp] :diminished :minor :flat] "F#m7b9b5")
  (check [["F" :sharp] :augmented :minor :sharp] "F#m7#9#5")
  (check [["F" :sharp] :diminished :minor :sharp] "F#m7#9b5"))

(deftest edges
  (check [nil] "")
  (check [["B" :flat] :minor :major :flat] "Bb-maj7b9"))
