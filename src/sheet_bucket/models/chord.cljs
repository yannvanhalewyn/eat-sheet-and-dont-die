(ns sheet-bucket.models.chord
  (:require [cljs.spec :as s]
            [clojure.test.check.generators]
            [cljs.spec.impl.gen :as gen]))

(defn rand-str [n]
  (clojure.string/join
   (take n (repeatedly #(.toString (rand-int 16) 16)))))

(def gen-id #(->> (s/gen string?) (gen/fmap (partial rand-str 5))))

(def min-maj? #{:minor :major})

(s/def ::id (s/spec (s/and string? #(= 5 (count %)))
                    :gen gen-id))
(s/def ::root #{:a :b :c :d :e :f :g})
(s/def ::triad min-maj?)
(s/def ::seventh min-maj?)
(s/def ::extension #{:nineth :thirteenth})
(s/def ::chord (s/keys :req-un [::root ::id]
                       :opt-un [::triad ::seventh ::extension]))

(s/def ::bar (s/coll-of ::chord :max-count 4 :min-count 1))
(s/def ::row (s/coll-of ::bar :max-count 6 :min-count 1))

(def gen #(gen/sample (s/gen ::chord) %))

(def gen-row #(gen/sample (s/gen ::row) %))
