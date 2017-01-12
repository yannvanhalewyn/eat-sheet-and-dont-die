(ns cards.chord
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.chord :as chord])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [cards.core :refer [defcard-with-props]]))

(defn- chord [props]
  (reagent/as-element (chord/component props)))

(defcard-doc
  "# Chord
## Example props"
  {:root "G" :triad :major :seventh :minor :nineth :major}
  "## An editable chord"
  {:focused true :chord-text "abc"})

;; Triads
(defcard-with-props Major (chord {:root "a"}))
(defcard-with-props Minor (chord {:root "b" :triad :minor}))

;; Sevenths
(defcard-with-props Seventh (chord {:root "C" :triad :major :seventh :minor}))
(defcard-with-props Minor-Seventh (chord {:root "D" :triad :minor :seventh :minor}))
(defcard-with-props Major-Seventh (chord {:root "E" :triad :major :seventh :major}))
(defcard-with-props Major-Seventh (chord {:root "E" :triad :major :seventh :major}))
(defcard-with-props Minor-Major-Seventh (chord {:root "E" :triad :minor :seventh :major}))

;; Nineths
(defcard-with-props Nineth (chord {:root "F" :triad :major :seventh :minor :nineth :minor}))
(defcard-with-props Minor-Nineth (chord {:root "F" :triad :minor :seventh :minor :nineth :minor}))
(defcard-with-props Major-Nineth (chord {:root "G" :triad :major :seventh :minor :nineth :major}))

;; Editing
(defcard-with-props Focused (chord {:focused true :chord-text "a-maj"}))
