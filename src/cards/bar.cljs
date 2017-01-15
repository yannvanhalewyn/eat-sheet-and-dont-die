(ns cards.bar
  (:require [reagent.core :as reagent]
            [cards.util :as util :refer [unparse-chord]]
            [sheet-bucket.components.bar :refer [component]]
            [sheet-bucket.models.chord :as chord]
            [sheet-bucket.util.util :refer [gen]])
  (:require-macros [cards.core :refer [defcard-props]]
                   [devcards.core :as dc :refer [defcard-doc defcard-rg]]))

(defn gen-with-root [n]
  (map #(unparse-chord (assoc % :root (first (gen ::chord/root 1))))
       (chord/gen n)))

(defcard-doc
  "# Bar"
  "Represents a bar, or a line measure of multiple chords"
  "## Props"
  (take 2 (gen-with-root 4)))

(defcard-props SingleChord
  [component {:chords [{:id 1 :raw "a"}]}])

(defonce chords (gen-with-root 2))
(defcard-rg TwoChords
  [component {:chords chords}])

(defonce chords-2 (gen-with-root 2))
(defcard-rg ThreeChords
  [component {:chords chords-2}])

(defonce chords-3 (gen-with-root 2))
(defcard-rg FourChords
  "It should show an alert on click"
  [component {:chords chords-3 :on-chord-click js/alert}])

(defonce chords-4 (update (vec (gen-with-root 4)) 1 assoc :root nil))
(defcard-rg with-empty-chord
  "It should have an empty space in second position"
  [component {:chords chords-4 :on-chord-click js/alert}])

(defn on-chord-update [id new-val] (.log js/console (str "Update - id: " id ", value: " new-val)))
(defonce chords-5 (gen-with-root 4))
(defcard-props With-selected-chord
  "Blurring the edit field should log out the bar id and the current
  value in the console."
  [component {:chords chords-5
              :selected (:id (second chords-5))
              :on-chord-update on-chord-update}])
