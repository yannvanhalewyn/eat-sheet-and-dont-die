(ns cards.chord
  (:require [sheet-bucket.components.chord :as chord])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-doc]]))

(defcard-doc
  "# Chord 
## Example props"
  {:root "G" :triad :major :seventh :minor :nineth :major})

;; Triads
(defcard Major (dc/reagent (chord/component {:root "a"})))
(defcard Minor (dc/reagent (chord/component {:root "b" :triad :minor})))

;; Sevenths
(defcard Seventh (dc/reagent (chord/component {:root "C" :triad :major :seventh :minor})))
(defcard Minor-Seventh (dc/reagent (chord/component {:root "D" :triad :minor :seventh :minor})))
(defcard Major-Seventh (dc/reagent (chord/component {:root "E" :triad :major :seventh :major})))
(defcard Major-Seventh (dc/reagent (chord/component {:root "E" :triad :major :seventh :major})))
(defcard Minor-Major-Seventh (dc/reagent (chord/component {:root "E" :triad :minor :seventh :major})))

;; Nineths
(defcard Nineth (dc/reagent (chord/component {:root "F" :triad :major :seventh :minor :nineth :minor})))
(defcard Minor-Nineth (dc/reagent (chord/component {:root "F" :triad :minor :seventh :minor :nineth :minor})))
(defcard Major-Nineth (dc/reagent (chord/component {:root "G" :triad :major :seventh :minor :nineth :major})))
