(ns sheet-bucket.specs.editor
  (:require [cljs.spec :as s]
            [clojure.test.check.generators]
            [cljs.spec.impl.gen :as gen]))

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
(s/def ::bar (s/coll-of ::chord :max-count 4 :min-count 1))
(s/def ::row (s/coll-of ::bar :max-count 6 :min-count 1))
(s/def ::rows (s/coll-of ::row :max-count 6 :min-count 1))
(s/def ::name (s/spec string? :gen #(s/gen #{"Intro" "Verse" "Chorus"})))
(s/def ::section (s/keys :req-un [::rows ::name]))
(s/def ::sections (s/coll-of ::section :max-count 6 :min-count 1))
(s/def ::title (s/spec string? :gen #(s/gen #{"Whole lotta love" "Breathe" "Lean on me"})))
(s/def ::artist (s/spec string? :gen #(s/gen #{"Led Zeppelin" "Pink Floyd" "Bill Withers"})))
(s/def ::sheet (s/keys :req-un [::title ::artist ::sections]))
