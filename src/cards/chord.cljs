(ns cards.chord
  (:require [cards.util :refer [alert]]
            [sheet-bucket.components.chord :refer [displayed-chord editable-chord]])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [cards.core :refer [defcard-with-props]]))

(defcard-doc
  "# Chords"
  "Chords are one of the main components in Sheet Bucket. Without
  them, the music would not be heard.")

;; Editing
(defcard-doc
  "## Editable Chords"
  "These are the chord input fields that the user can use to edit a
  chord. It should launch an alert when focus is lost (eg: user
  stopped editing)")

(defcard-with-props Editable
  "This chord should be focused on page load."
  [editable-chord {:text "a-maj"}])

(defcard-doc
  "## Display Chord"
  "This is a chord in it's rest state. This is what the user will see
  when it's not editing the chord.")

(defcard-with-props Major
  "Clicking the chord should launch an alert"
  [displayed-chord {:root "A" :on-click (alert "click")}])

;; Triads
(defcard-with-props Minor
  "Note: root is case-insensitive"
  [displayed-chord {:root "b" :triad :minor}])

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
