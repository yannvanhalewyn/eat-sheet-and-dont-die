(ns frontend.specs.editor
  (:require [frontend.models.sheet :as sheet]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators]
            [clojure.spec.gen.alpha :as gen]))

(defn gen-id []
  (gen/fmap (fn [_] (sheet/gen-temp-id)) (gen/any)))

;; Chord
;; =====
(def root? #{"A" "B" "C" "D" "E" "F" "G" "1" "2" "3" "4" "5" "6" "7"})
(def accidental? #{:flat :sharp :natural})
(def extension? #{9 13})

(s/def :db/id (s/spec (s/or :datomic int? :tmp-id string?) :gen gen-id))
(s/def :coll/position int?)
(s/def :chord/root (s/tuple root? accidental?))
(s/def :chord/triad #{:minor :major :augmented :diminished})
(s/def :chord/seventh #{:minor :major})
(s/def :chord/ninth accidental?)
(s/def ::chord (s/keys :req [:db/id :coll/position]
                 :opt [:chord/root :chord/triad :chord/seventh :chord/ninth]))

;; Sheet
;; =====

(s/def :bar/chords (s/coll-of ::chord :min-count 1 :gen-max 4))
(s/def :bar/start-repeat boolean?)
(s/def :bar/end-repeat boolean?)
(s/def ::bar (s/keys :req [:db/id :coll/position :bar/chords :bar/start-repeat :bar/end-repeat]))

(s/def :row/bars (s/coll-of ::bar :min-count 1 :gen-max 3))
(s/def ::row (s/keys :req [:db/id :coll/position :row/bars]))

(s/def :section/title (s/spec string? :gen #(s/gen #{"Intro" "Verse" "Chorus"})))
(s/def :section/rows (s/coll-of ::row :min-count 1 :gen-max 3))
(s/def ::section (s/keys :req [:db/id :coll/position :section/title :section/rows]))

(s/def :sheet/title (s/spec string? :gen #(s/gen #{"Whole lotta love" "Breathe" "Lean on me"})))
(s/def :sheet/artist (s/spec string? :gen #(s/gen #{"Led Zeppelin" "Pink Floyd" "Bill Withers"})))
(s/def :sheet/sections (s/coll-of ::section :min-count 1 :gen-max 3))
(s/def ::sheet (s/keys :req [:db/id :sheet/title :sheet/artist :sheet/sections]))
