(ns cards.chord
  (:require [cards.util :refer [alert]]
            [sheet-bucket.components.chord :refer [displayed-chord editable-chord]])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [cards.core :refer [defcard-props]]))

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

(defcard-props Editable
  "This chord should be focused on page load.\n
   Blurring this field should alert the message with the current input's value"
  [editable-chord {:text "a-maj" :on-blur js/alert}])

(defcard-doc
  "## Display Chord"
  "This is a chord in it's rest state. This is what the user will see
  when it's not editing the chord.")

(defcard-props Major
  "Clicking the chord should launch an alert"
  [displayed-chord {:root :a :on-click (alert "click")}])

;; Triads
(defcard-props Minor
  "Note: root is case-insensitive"
  [displayed-chord {:root :b :triad :minor}])

;; Sevenths
(defcard-props Seventh
  [displayed-chord {:root :c :triad :major :seventh :minor}])

(defcard-props Minor-Seventh
  [displayed-chord {:root :d :triad :minor :seventh :minor}])

(defcard-props Major-Seventh
  [displayed-chord {:root :e :triad :major :seventh :major}])

(defcard-props Major-Seventh
  [displayed-chord {:root :e :triad :major :seventh :major}])

(defcard-props Minor-Major-Seventh
  [displayed-chord {:root :e :triad :minor :seventh :major}])

;; Nineths
(defcard-props Nineth
  [displayed-chord {:root :f :triad :major :seventh :minor :nineth :minor}])

(defcard-props Minor-Nineth
  [displayed-chord {:root :f :triad :minor :seventh :minor :nineth :minor}])

(defcard-props Major-Nineth
  [displayed-chord {:root :g :triad :major :seventh :minor :nineth :major}])
