(ns frontend.specs.editor
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators]
            [clojure.spec.gen.alpha :as gen]))

(defn rand-str [n]
  (clojure.string/join
    (take n (repeatedly #(.toString (rand-int 16) 16)))))

(def gen-id #(->> (s/gen string?) (gen/fmap (partial rand-str 5))))

;; Chord
;; =====
(def root? #{"A" "B" "C" "D" "E" "F" "G" "1" "2" "3" "4" "5" "6" "7"})
(def accidental? #{:flat :sharp :natural})
(def extension? #{9 13})

(s/def :chord/id (s/spec (s/and string? #(= 5 (count %)))
                         :gen gen-id))
(s/def :chord/root (s/tuple root? accidental?))
(s/def :chord/triad #{:minor :major :augmented :diminished})
(s/def :chord/seventh #{:minor :major})
(s/def :chord/ninth accidental?)
(s/def ::chord (s/keys :req [:chord/id]
                       :opt [:chord/root :chord/triad :chord/seventh :chord/ninth]))

;; Sheet
;; =====

(s/def :bar/chords (s/coll-of ::chord :min-count 1 :gen-max 4))
(s/def ::bar (s/keys :req [:bar/chords]))

(s/def :row/bars (s/coll-of ::bar :min-count 1 :gen-max 4))
(s/def ::row (s/keys :req [:row/bars]))

(s/def :section/title (s/spec string? :gen #(s/gen #{"Intro" "Verse" "Chorus"})))
(s/def :section/rows (s/coll-of ::row :min-count 1 :gen-max 3))
(s/def ::section (s/keys :req [:section/title :section/rows]))

(s/def :sheet/title (s/spec string? :gen #(s/gen #{"Whole lotta love" "Breathe" "Lean on me"})))
(s/def :sheet/artist (s/spec string? :gen #(s/gen #{"Led Zeppelin" "Pink Floyd" "Bill Withers"})))
(s/def :sheet/sections (s/coll-of ::section :min-count 1 :gen-max 3))
(s/def ::sheet (s/keys :req [:sheet/title :sheet/artist :sheet/sections]))