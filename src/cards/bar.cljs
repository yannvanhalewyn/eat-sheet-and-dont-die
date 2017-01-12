(ns cards.bar
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.bar :refer [component]])
  (:require-macros [cards.core :refer [defcard-props]]
                   [devcards.core :as dc :refer [defcard-doc defcard-rg]]))

(def chords
  [{:id "chord-1" :root "a"}
   {:id "chord-2" :root "c" :triad :minor}
   {:id "chord-3" :root "E"}
   {:id "chord-4" :root "c" :triad :minor}])

(defcard-doc
  "# Bar"
  "Represents a bar, or a line measure of multiple chords"
  "## Props"
  chords)

(defcard-props SingleChord
  [component {:chords [{:root "a"}]}])

(defcard-props TwoChords
  [component {:chords [{:root "a"} {:root "c" :triad :minor}]}])

(defcard-rg ThreeChords
  [component {:chords (take 3 chords)}])

(defcard-rg FourChords
  [component {:chords chords}])

(defcard-props With-current-chord
  [component {:chords chords}])
