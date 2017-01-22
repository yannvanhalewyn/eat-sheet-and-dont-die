(ns sheet-bucket.models.chord
  (:require [cljs.core.match :refer-macros [match]]
            [goog.string :refer [format contains caseInsensitiveContains]]
            [goog.string.format]))

;; Negative lookahead for ending accidental that are part of "b5", like Eb5 -> root = E
(def root-regx (str "([#b])?([a-gA-G1-7])([#b])?(?!5)"))
;; Negative lookahead for 'm' that is not part of 'maj'
(def triad-regx (str "min|m(?!aj)|-|aug|\\+|#5|b5"))
(def extension-regx (str "(7|maj|Maj)?7?([#b]?9)?([#b]5)?"))
(def chord-regex (re-pattern (format "%s(%s)?(%s)?"
                                     root-regx triad-regx extension-regx)))

(defn parse
  "Parses a raw chord string to chord data"
  [s]
  (let [result (rest (re-find chord-regex s))
        [_ root _ triad extension seventh ninth fifth] result]
    {:root (when root
             (match (vec (take 3 result))
               ["b" root _] [root :flat]
               ["#" root _] [root :sharp]
               [_ root "b"] [root :flat]
               [_ root "#"] [root :sharp]
               :else [root]))
     :triad (or (case fifth
                  "b5" :diminished
                  "#5" :augmented
                  nil)
                (match triad
                  (:or "m" "min" "-") :minor
                  (:or "aug" "+" "#5") :augmented
                  "b5" :diminished
                  :else :major))
     :seventh (cond
                (caseInsensitiveContains (or extension "") "maj") :major
                extension :minor
                :else nil)
     :ninth (match ninth
              "9" :natural
              "b9" :flat
              "#9" :sharp
              :else nil)}))
