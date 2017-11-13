(ns frontend.models.chord-test
  (:require [frontend.models.chord :refer [parse]]
            [goog.string :refer [format]]
            [cljs.test :refer-macros [deftest is testing]]
            [clojure.spec.test.alpha :as stest]))

(stest/check `parse)

(def roots ["a" "b" "c" "d" "e" "f" "g"
            "A" "B" "C" "D" "E" "F" "G"
            "1" "2" "3" "4" "5" "6" "7"])

(defn check [[root triad seventh ninth] raw]
  (let [result (parse raw)
        run #(is (= %1 (%2 result))
               (format "%s - expected %s from %s, got: %s"
                 (name %2) %1 raw (%2 result)))]
    (run root :chord/root)
    (run (or triad :major) :chord/triad)
    (run seventh :chord/seventh)
    (run ninth :chord/ninth)))

(deftest roots-test
  (every? #(check [[% :natural] :major] %) roots)
  (every? #(check [[% :flat] :major] (str "b" %)) roots)
  (every? #(check [[% :flat] :major] (str % "b")) roots)
  (every? #(check [[% :sharp] :major] (str "#" %)) roots)
  (every? #(check [[% :sharp] :major] (str % "#")) roots))

(deftest triads
  (check [["a" :natural] :minor] "am")
  (check [["b" :natural] :minor] "b-")
  (check [["c" :natural] :minor] "cmin")
  (check [["d" :natural] :augmented] "daug")
  (check [["e" :natural] :augmented] "e+")
  (check [["e" :natural] :augmented] "e#5")
  (check [["B" :flat] :diminished] "Bbb5")
  (check [["B" :natural] :diminished] "Bb5"))

(deftest sevenths
  (check [["a" :natural] :major :minor] "a7")
  (check [["g" :natural] :minor :minor] "g-7")
  (check [["7" :natural] :major :minor] "77")
  (check [["B" :natural] :major :minor] "B7")
  (check [["C" :natural] :major :major] "Cmaj")
  (check [["A" :flat] :major :major] "Abmaj7")
  (check [["D" :flat] :minor :major] "Dbminmaj7")
  (check [["F" :flat] :minor :major] "FbminMaj7")
  (check [["C" :natural] :minor :major] "C-maj7")
  (check [["F" :sharp] :diminished :minor] "F#m7b5")
  (check [["F" :sharp] :augmented :minor] "F#m7#5"))

(deftest ninths
  (check [["B" :natural] :major :minor :natural] "B9")
  (check [["C" :natural] :minor :major :natural] "C-maj9")
  (check [["D" :natural] :major :minor :sharp] "D7#9")
  (check [["D" :natural] :major :minor :flat] "D7b9")
  (check [["F" :sharp] :augmented :minor :flat] "F#m7b9#5")
  (check [["F" :sharp] :diminished :minor :flat] "F#m7b9b5")
  (check [["F" :sharp] :augmented :minor :sharp] "F#m7#9#5")
  (check [["F" :sharp] :diminished :minor :sharp] "F#m7#9b5"))

(deftest edges
  (check [nil] "")
  (check [["B" :flat] :minor :major :flat] "Bb-maj7b9"))
