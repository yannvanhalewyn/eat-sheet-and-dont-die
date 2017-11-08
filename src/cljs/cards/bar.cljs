(ns cards.bar
  (:require [reagent.core :as reagent]
            [cards.util :as util :refer [unparse-chord]]
            [frontend.views.bar :refer [component]]
            [frontend.specs.editor :as specs]
            [frontend.util.util :refer [gen]])
  (:require-macros [cards.core :refer [defcard-props]]
                   [devcards.core :as dc :refer [defcard-doc defcard-rg]]))

(defn gen-with-root [n]
  (map #(unparse-chord (assoc % :chord/root (first (gen :chord/root 1))))
       (gen ::specs/chord n)))

(defcard-doc
  "# Bar"
  "Represents a bar, or a line measure of multiple chords"
  "## Props"
  {:bar/chords (take 2 (gen-with-root 4))})

(defcard-props SingleChord
  [component {:bar {:bar/chords [{:chord/id 1 :chord/value "a"}]}}])

(defonce chords (gen-with-root 2))
(defcard-rg TwoChords
  [component {:bar {:bar/chords chords}}])

(defonce chords-2 (gen-with-root 2))
(defcard-rg ThreeChords
  [component {:bar {:bar/chords chords-2}}])

(defonce chords-3 (gen-with-root 2))
(defcard-rg FourChords
  "It should show an alert on click"
  [component {:bar {:bar/chords chords-3} :on-chord-click js/alert}])

(defonce chords-4 (update (vec (gen-with-root 4)) 1 assoc :root nil))
(defcard-rg with-empty-chord
  "It should have an empty space in second position"
  [component {:bar {:bar/chords chords-4} :on-chord-click js/alert}])

(defn on-chord-update [id new-val]
  (.log js/console (str "Update - id: " id ", value: " new-val)))

(defonce chords-5 (gen-with-root 4))
(defcard-props With-selected-chord
  "Blurring the edit field should log out the bar id and the current
  value in the console."
  [component {:bar {:bar/chords chords-5}
              :selected (:chord/id (second chords-5))
              :update-chord on-chord-update}])
