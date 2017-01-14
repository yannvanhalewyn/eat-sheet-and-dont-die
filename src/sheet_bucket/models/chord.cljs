(ns sheet-bucket.models.chord
  (:require [cljs.spec :as s]
            [cljs.core.match :refer-macros [match]]
            [clojure.test.check.generators]
            [goog.string :refer [format]]
            [goog.string.format]
            [cljs.spec.impl.gen :as gen]))

(defn rand-str [n]
  (clojure.string/join
   (take n (repeatedly #(.toString (rand-int 16) 16)))))

(def gen-id #(->> (s/gen string?) (gen/fmap (partial rand-str 5))))

(def root-val? #{"A" "B" "C" "D" "E" "F" "G" "1" "2" "3" "4" "5" "6" "7"})
(def accidental? #{:flat :sharp})

(s/def ::id (s/spec (s/and string? #(= 5 (count %)))
                    :gen gen-id))
(s/def ::root (s/tuple root-val? (s/or :nil nil? :accidental accidental?)))
(s/def ::triad #{:minor :major :augmented :diminished})
(s/def ::seventh #{:minor :major})
(s/def ::extension #{:nineth :thirteenth})
(s/def ::chord (s/keys :req-un [::id]
                       :opt-un [::root ::triad ::seventh ::extension]))

(s/def ::bar (s/coll-of ::chord :max-count 4 :min-count 1))
(s/def ::row (s/coll-of ::bar :max-count 6 :min-count 1))

(def gen #(gen/sample (s/gen ::chord) %))

(def gen-row #(gen/sample (s/gen ::row) %))

;; Negative lookahead for ending accidental that are part of "b5", like Eb5 -> root = E
(def root-regx (str "([#b])?([a-gA-G1-7])([#b])?(?!5)"))
;; Negative lookahead for 'm' that is not part of 'maj'
(def triad-regx (str "min|m(?!aj)|-|aug|\\+|b5"))
(def seventh-regx (str "7|maj|Maj"))
(def chord-regex (re-pattern (format "%s(%s)?(%s)?"
                                     root-regx triad-regx seventh-regx)))

(defn parse
  "Parses a raw chord string to chord data"
  [s]
  (let [result (rest (re-find chord-regex s))
        [_ root _ triad seventh] result]
    {:root (match (vec (take 3 result))
             ["b" root _] [root :flat]
             ["#" root _] [root :sharp]
             [_ root "b"] [root :flat]
             [_ root "#"] [root :sharp]
             :else [root])
     :triad (match triad
              (:or "m" "min" "-") :minor
              (:or "aug" "+") :augmented
              "b5" :diminished
              :else :major)
     :seventh (match seventh
                "7" :minor
                (:or "maj" "Maj") :major
                :else nil)}))
