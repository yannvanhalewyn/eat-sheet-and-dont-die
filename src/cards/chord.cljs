(ns cards.chord
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.chord :as chord])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-doc]]))

(defn- chord [props]
  (reagent/as-element (chord/component props)))

(defcard-doc
  "# Chord 
## Example props"
  {:root "G" :triad :major :seventh :minor :nineth :major}
  "## An editable chord"
  {:focused true :chord-text "abc"})

;; Triads
(defcard Major (chord {:root "a"}))
(defcard Minor (chord {:root "b" :triad :minor}))

;; Sevenths
(defcard Seventh (chord {:root "C" :triad :major :seventh :minor}))
(defcard Minor-Seventh (chord {:root "D" :triad :minor :seventh :minor}))
(defcard Major-Seventh (chord {:root "E" :triad :major :seventh :major}))
(defcard Major-Seventh (chord {:root "E" :triad :major :seventh :major}))
(defcard Minor-Major-Seventh (chord {:root "E" :triad :minor :seventh :major}))

;; Nineths
(defcard Nineth (chord {:root "F" :triad :major :seventh :minor :nineth :minor}))
(defcard Minor-Nineth (chord {:root "F" :triad :minor :seventh :minor :nineth :minor}))
(defcard Major-Nineth (chord {:root "G" :triad :major :seventh :minor :nineth :major}))

;; Editing
(defcard Focused (chord {:focused true :chord-text "a-maj"}))
