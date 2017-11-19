(ns frontend.models.chord
  (:require [frontend.util.regex :as rx]
            [shared.specs :as specs]
            [clojure.core.match :refer-macros [match]]
            [clojure.spec.alpha :as s]
            [goog.string :refer [format contains]]
            [goog.string.format]
            [clojure.string :as str]))

(def chord-rx
  (let [accidental? (rx/maybe "[#b]")
        root-note (rx/or (rx/non-capturing-group accidental? "[0-7]")
                    (rx/non-capturing-group "[a-gA-G]" accidental?))]
    (rx/build
      ;; Root
      (rx/group root-note)
      ;; Triad, negative lookahead for 'm' that is not part of 'maj'
      (rx/maybe-group (rx/or "min" (str "m" (rx/neg-lookahead "aj")) "-" "aug" "\\+" "dim"))
      ;; Seventh
      (rx/maybe-group
        (rx/maybe-group-non-capturing (rx/or "maj" "Maj"))
        (rx/maybe "7"))
      ;; Extensions
      (rx/group (rx/multi
                  (rx/non-capturing-group
                    accidental?
                    (rx/non-capturing-group (rx/or "4" "5" "6" "9" "11" "13")))))
      ;; Sus
      (rx/maybe-group-non-capturing "sus" (rx/group "[24]"))
      ;; Bass
      (rx/maybe-group-non-capturing "\\/" (rx/group root-note)))))

(defn- parse-note [note-str]
  (when note-str
    (let [[_ acc1 note acc2] (re-find #"([#b])?([0-7a-gA-G])([#b])?" note-str)]
      [(str/upper-case note) (case (or acc1 acc2) "#" :sharp "b" :flat :natural)])))

(defn- parse-extension [ext-str]
  (let [[first & rest] ext-str
        rest (apply str rest)]
    (case first
      "#" [rest :sharp]
      "b" [rest :flat]
      [ext-str :natural])))

(defn parse
  "Parses a raw chord string to chord data"
  [s]
  (let [[_ root triad seventh extensions sus bass] (re-find chord-rx s)]
    {:chord/root (parse-note root)
     :chord/triad (match triad
                    (:or "m" "min" "-") :minor
                    (:or "aug" "+") :augmented
                    "dim" :diminished
                    :else :major)
     :chord/seventh (let [e (str/lower-case (or seventh ""))]
                      (cond
                        (contains e "maj") :natural
                        (contains e "7") :flat
                        :else nil))
     :chord/extensions (->> (str/split extensions #"([#b]?(?:4|5|6|9|11|13))")
                         (remove empty?)
                         (map parse-extension))
     :chord/sus sus
     :chord/bass (parse-note bass)}))

(s/fdef parse
  :args (s/cat :input :chord/value)
  :ret :chord/parsed
  :fn (fn [res] (= (specs/unparse-chord (:ret res)) (-> res :args :input))))
