(ns cards.chord
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.chord :refer [displayed-chord editable-chord]])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [cards.core :refer [defcard-with-props]]))

(defcard-doc "# Editable Chord")

;; Editing
(defcard-with-props Focused
  "This chord should be focused on page load."
  [editable-chord {:text "a-maj"}])

(defcard-doc
  "# Display Chord
## Example props"
  {:root "G" :triad :major :seventh :minor :nineth :major}
  "## An editable chord"
  {:focused true :chord-text "abc"})

;; Triads
(defcard-with-props Major [displayed-chord {:root "a"}])
(defcard-with-props Minor [displayed-chord {:root "b" :triad :minor}])

;; Sevenths
(defcard-with-props Seventh
  [displayed-chord {:root "C" :triad :major :seventh :minor}])

(defcard-with-props Minor-Seventh
  [displayed-chord {:root "D" :triad :minor :seventh :minor}])

(defcard-with-props Major-Seventh
  [displayed-chord {:root "E" :triad :major :seventh :major}])

(defcard-with-props Major-Seventh
  [displayed-chord {:root "E" :triad :major :seventh :major}])

(defcard-with-props Minor-Major-Seventh
  [displayed-chord {:root "E" :triad :minor :seventh :major}])

;; Nineths
(defcard-with-props Nineth
  [displayed-chord {:root "F" :triad :major :seventh :minor :nineth :minor}])

(defcard-with-props Minor-Nineth
  [displayed-chord {:root "F" :triad :minor :seventh :minor :nineth :minor}])

(defcard-with-props Major-Nineth
  [displayed-chord {:root "G" :triad :major :seventh :minor :nineth :major}])
