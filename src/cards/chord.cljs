(ns cards.chord
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.chord :refer [component]])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [cards.core :refer [defcard-with-props]]))

(defcard-doc
  "# Chord
## Example props"
  {:root "G" :triad :major :seventh :minor :nineth :major}
  "## An editable chord"
  {:focused true :chord-text "abc"})

;; Triads
(defcard-with-props Major [component {:root "a"}])
(defcard-with-props Minor [component {:root "b" :triad :minor}])

;; Sevenths
(defcard-with-props Seventh
  [component {:root "C" :triad :major :seventh :minor}])

(defcard-with-props Minor-Seventh
  [component {:root "D" :triad :minor :seventh :minor}])

(defcard-with-props Major-Seventh
  [component {:root "E" :triad :major :seventh :major}])

(defcard-with-props Major-Seventh
  [component {:root "E" :triad :major :seventh :major}])

(defcard-with-props Minor-Major-Seventh
  [component {:root "E" :triad :minor :seventh :major}])

;; Nineths
(defcard-with-props Nineth
  [component {:root "F" :triad :major :seventh :minor :nineth :minor}])

(defcard-with-props Minor-Nineth
  [component {:root "F" :triad :minor :seventh :minor :nineth :minor}])

(defcard-with-props Major-Nineth
  [component {:root "G" :triad :major :seventh :minor :nineth :major}])

;; Editing
(defcard-with-props Focused
  "This chord should be focused on page load."
  [component {:focused true :chord-text "a-maj"}])
