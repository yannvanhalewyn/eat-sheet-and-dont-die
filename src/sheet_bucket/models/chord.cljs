(ns sheet-bucket.models.chord
  (:require [cljs.spec :as s]
            [clojure.test.check.generators]
            [cljs.spec.impl.gen :as gen]))

(def rand-chars (repeatedly #(.toString (rand-int 16) 16)))
(def rand-str #(clojure.string/join (take % rand-chars)))
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

(def gen #(gen/sample (s/gen ::chord) %))
