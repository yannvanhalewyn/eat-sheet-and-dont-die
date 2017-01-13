(ns cards.bar
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.bar :refer [component]]
            [sheet-bucket.models.chord :as chord])
  (:require-macros [cards.core :refer [defcard-props]]
                   [devcards.core :as dc :refer [defcard-doc defcard-rg]]))

(defcard-doc
  "# Bar"
  "Represents a bar, or a line measure of multiple chords"
  "## Props"
  (chord/gen 2))

(defcard-props SingleChord
  [component {:chords [{:id 1 :root "a"}]}])

(defcard-rg TwoChords
  [component {:chords (chord/gen 2)}])

(defcard-rg ThreeChords
  [component {:chords (chord/gen 3)}])

(defcard-rg FourChords
  "It should show an alert on click"
  [component {:chords (chord/gen 4) :on-chord-click js/alert}])

(def on-chord-update #(js/alert (str %1 " got updated with " %2)))
(defcard-props With-selected-chord
  "Blurring the edit field should fire an alert with the bar id and
  the current value"
  [component {:chords (chord/gen 4)
              :selected "chord-2"
              :on-chord-update on-chord-update}])
