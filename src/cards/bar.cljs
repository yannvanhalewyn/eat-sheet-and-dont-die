(ns cards.bar
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.bar :refer [component]]
            [sheet-bucket.models.chord :as chord])
  (:require-macros [cards.core :refer [defcard-props]]
                   [devcards.core :as dc :refer [defcard-doc defcard-rg]]))

(defonce chords (chord/gen 4))
(defcard-doc
  "# Bar"
  "Represents a bar, or a line measure of multiple chords"
  "## Props"
  (take 2 chords))

(defcard-props SingleChord
  [component {:chords [{:id 1 :root "a"}]}])

(defonce chords-2 (chord/gen 2))
(defcard-rg TwoChords
  [component {:chords chords-2}])

(defonce chords-3 (chord/gen 2))
(defcard-rg ThreeChords
  [component {:chords chords-3}])

(defonce chords-4 (chord/gen 2))
(defcard-rg FourChords
  "It should show an alert on click"
  [component {:chords chords-4 :on-chord-click js/alert}])

(def on-chord-update #(js/alert (str %1 " got updated with " %2)))
(defcard-props With-selected-chord
  "Blurring the edit field should fire an alert with the bar id and
  the current value"
  [component {:chords chords
              :selected (:id (second chords))
              :on-chord-update on-chord-update}])
