(ns shared.specs
  (:require [shared.utils :refer [gen-temp-id presence]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

;; Generators
;; ==========
(defn- number-str? [s]
  #?(:cljs
     (not (js/isNaN (js/parseInt s)))))

(defn- root->str [[note acc]]
  (let [acc (case acc :flat "b" :sharp "#" "")]
    (if (number-str? note)
      (str acc note)    ;; b3
      (str note acc)))) ;; Eb

(defn unparse-chord
  "Takes a parsed chord and returns a possible raw input value for that chord."
  [{:keys [:chord/root :chord/sus :chord/triad :chord/seventh :chord/ninth :chord/bass]}]
  (str
    (root->str root)
    (case triad :minor "-" :augmented "+" :diminished "b5" "")
    (when-not (= :natural ninth)
      (case seventh :natural "Maj7" :flat "7" :diminished "dim" ""))
    (case ninth :natural "9" :flat "b9" :sharp "#9" "")
    (when sus (str "sus" sus))
    (when bass (str "/" (root->str bass)))))

(defn gen-id []
  (gen/fmap (fn [_] (gen-temp-id)) (gen/any)))

(defn gen-chord-value []
  (gen/fmap unparse-chord (s/gen :chord/parsed)))

;; Chord
;; =====
(def root? #{"A" "B" "C" "D" "E" "F" "G" "1" "2" "3" "4" "5" "6" "7"})
(def accidental? #{:flat :sharp :natural})
(def extension? #{9 13})

(s/def :db/id (s/spec (s/or :datomic pos-int? :tmp-id string?) :gen gen-id))
(s/def :coll/position nat-int?)
(s/def :chord/value (s/spec string? :gen gen-chord-value))
(s/def ::chord (s/keys :req [:db/id :coll/position :chord/value]))

;; Parsed chord
(s/def :chord/root (s/tuple root? accidental?))
(s/def :chord/triad #{:minor :major :augmented :diminished})
(s/def :chord/seventh (s/nilable #{:natural :flat :diminished}))
(s/def :chord/ninth (s/nilable accidental?))
(s/def :chord/bass (s/nilable :chord/root))
(s/def :chord/sus (s/nilable #{"2" "4"}))
(s/def :chord/parsed (s/keys :req [:chord/root
                                   :chord/sus
                                   :chord/triad
                                   :chord/seventh
                                   :chord/ninth
                                   :chord/bass]))

;; Sheet
;; =====

(s/def :bar/chords (s/coll-of ::chord :min-count 1 :gen-max 4))
(s/def :bar/start-repeat boolean?)
(s/def :bar/end-repeat boolean?)

;; Attachments
(s/def :coord/x int?)
(s/def :coord/y int?)
(s/def :attachment/type #{:symbol/coda :symbol/segno :attachment/textbox})
(s/def :textbox/value string?)

(s/def :attachment/symbol (s/keys :req [:db/id :coord/x :coord/y :attachment/type]))
(s/def :attachment/textbox (s/keys :req [:db/id :coord/x :coord/y
                                         :attachment/type :textbox/value]))
(s/def :bar/attachments (s/coll-of (s/or :symbol :attachment/symbol :texbox :attachment/textbox)
                          :gen-max 1))

(s/def ::bar (s/keys :req [:db/id :coll/position :bar/chords]
               :opt [:bar/start-repeat :bar/end-repeat :bar/attachments]))

(s/def :row/bars (s/coll-of ::bar :min-count 1 :gen-max 3))
(s/def ::row (s/keys :req [:db/id :coll/position :row/bars]))

(s/def :section/title (s/spec string? :gen #(s/gen #{"Intro" "Verse" "Chorus"})))
(s/def :section/rows (s/coll-of ::row :min-count 1 :gen-max 3))
(s/def ::section (s/keys :req [:db/id :coll/position :section/title :section/rows]))

(s/def :sheet/title (s/spec string? :gen #(s/gen #{"Whole lotta love" "Breathe" "Lean on me"})))
(s/def :sheet/artist (s/spec string? :gen #(s/gen #{"Led Zeppelin" "Pink Floyd" "Bill Withers"})))
(s/def :sheet/sections (s/coll-of ::section :min-count 1 :gen-max 3))
(s/def ::sheet (s/keys :req [:db/id :sheet/title :sheet/artist :sheet/sections]))

;; User
;; ====

(def email-regex
  #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(s/def :user/email (s/and string? #(re-matches email-regex %)))
(s/def :user/first-name presence)
(s/def :user/last-name presence)
(s/def ::user (s/keys :req [:db/id :user/email :user/first-name :user/last-name]))
