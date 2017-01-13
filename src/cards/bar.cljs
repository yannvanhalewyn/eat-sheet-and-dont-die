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

(defn on-chord-update [id new-val] (.log js/console (str "Update - id: " id ", value: " new-val)))
(defcard-props With-selected-chord
  "Blurring the edit field should log out the bar id and the current
  value in the console."
  [component {:chords chords
              :selected (:id (second chords))
              :on-chord-update on-chord-update}])
