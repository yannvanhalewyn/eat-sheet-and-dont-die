(ns sheet-bucket.specs.editor
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

(s/def ::id (s/spec (s/and string? #(= 5 (count %)))
                    :gen gen-id))
(s/def ::root (s/tuple root? accidental?))
(s/def ::triad #{:minor :major :augmented :diminished})
(s/def ::seventh #{:minor :major})
(s/def ::ninth accidental?)
(s/def ::chord (s/keys :req-un [::id]
                       :opt-un [::root ::triad ::seventh ::ninth]))

;; Sheet
;; =====
(s/def :section/name (s/spec string? :gen #(s/gen #{"Intro" "Verse" "Chorus"})))
(s/def :sheet/title (s/spec string? :gen #(s/gen #{"Whole lotta love" "Breathe" "Lean on me"})))
(s/def :sheet/artist (s/spec string? :gen #(s/gen #{"Led Zeppelin" "Pink Floyd" "Bill Withers"})))

(s/def ::bar (s/tuple (s/coll-of ::chord :min-count 1 :gen-max 4)))
(s/def ::row (s/tuple (s/coll-of ::bar :min-count 1 :gen-max 4)))
(s/def ::section (s/tuple (s/coll-of ::row :min-count 1 :gen-max 3)
                          (s/keys :req-un [:section/name])))
(s/def ::sheet (s/tuple (s/coll-of ::section :min-count 1 :gen-max 3)
                        (s/keys :req-un [:sheet/title :sheet/artist])))
