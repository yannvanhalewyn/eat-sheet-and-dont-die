(ns cards.bar
  (:require [reagent.core :as reagent]
            [frontend.views.bar :refer [component]]
            [frontend.specs.editor :as specs]
            [frontend.util.util :refer [gen]])
  (:require-macros [cards.core :refer [defcard-props]]
                   [devcards.core :as dc :refer [defcard-doc defcard-rg]]))

(defonce chords (repeatedly #(first (gen ::specs/chord 1))))

(defcard-doc
  "# Bar"
  "Represents a bar, or a line measure of multiple chords"
  "## Props"
  {:bar/chords (take 2 chords)})

(defcard-props SingleChord
  [component {:bar {:bar/chords [{:db/id 1 :chord/value "a"}]}}])

(defcard-rg TwoChords
  [component {:bar {:bar/chords (take 2 chords)}}])

(defcard-rg ThreeChords
  [component {:bar {:bar/chords (take 3 chords)}}])

(defcard-rg FourChords
  "It should show an alert on click"
  [component {:bar {:bar/chords (take 4 chords)} :on-chord-click js/alert}])

(defcard-rg with-empty-chord
  "It should have an empty space in second position"
  [component {:bar {:bar/chords (update (vec (take 4 chords)) 1 assoc :root nil)}
              :on-chord-click js/alert}])

(defn on-chord-update [id new-val]
  (.log js/console (str "Update - id: " id ", value: " new-val)))

(defcard-props With-selected-chord
  "Blurring the edit field should log out the bar id and the current
  value in the console."
  [component {:bar {:bar/chords (take 5 chords)}
              :selected (:db/id (second (vec (take 2 chords))))
              :update-chord on-chord-update}])
