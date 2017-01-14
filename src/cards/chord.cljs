(ns cards.chord
  (:require [cards.util :refer [alert]]
            [sheet-bucket.models.chord :refer [parse]]
            [sheet-bucket.components.chord :refer [displayed-chord editable-chord]]
            [reagent.core :as r])
  (:require-macros [devcards.core :refer [defcard-doc defcard-rg]]
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
  "This chord should be focused on page load. Blurring this field
  should log the message with the current input's value"
  [editable-chord {:text "a-maj" :on-blur (.-log js/console)}])

(defcard-doc
  "## Display Chord"
  "This is a chord in it's rest state. This is what the user will see
  when it's not editing the chord.")

(def roots ["A" "B" "C" "D" "E" "F" "G" "1" "2" "3" "4" "5" "6" "7"])

(defcard-rg roots
  "All the natural roots"
  [:div {:style {:display :flex :justify-content :space-between}}
   (for [root roots]
     ^{:key root} [displayed-chord {:root [root]}])])

(defcard-rg flats
  "All the flat roots"
  [:div {:style {:display :flex :justify-content :space-between}}
   (for [root roots]
     ^{:key root} [displayed-chord {:root [root :flat]}])])

(defcard-rg sharps
  "All the sharp roots"
  [:div {:style {:display :flex :justify-content :space-between}}
   (for [root roots]
     ^{:key root} [displayed-chord {:root [root :sharp]}])])

(defcard-props Major
  "Clicking the chord should launch an alert"
  [displayed-chord {:root ["A" :flat] :on-click (alert "click")}])

;; Triads
(defcard-props Minor
  "Note: root is case-insensitive"
  [displayed-chord {:root ["B"] :triad :minor}])

;; Sevenths
(defcard-props Seventh
  [displayed-chord {:root ["C"] :triad :major :seventh :minor}])

(defcard-props Minor-Seventh
  [displayed-chord {:root ["D"] :triad :minor :seventh :minor}])

(defcard-props Major-Seventh
  [displayed-chord {:root ["E"] :triad :major :seventh :major}])

(defcard-props Major-Seventh
  [displayed-chord {:root ["F"] :triad :major :seventh :major}])

(defcard-props Minor-Major-Seventh
  [displayed-chord {:root ["1"] :triad :minor :seventh :major}])

;; Ninths
(defcard-props Ninth
  [displayed-chord {:root ["F" :sharp] :triad :major :seventh :minor :ninth :minor}])

(defcard-props Minor-Ninth
  [displayed-chord {:root ["5"] :triad :minor :seventh :minor :ninth :minor}])

(defcard-props Major-Ninth
  [displayed-chord {:root ["3" :flat] :triad :major :seventh :minor :ninth :major}])

(defcard-props No-root
  "Should not display anything"
  [displayed-chord {:root nil :triad :major :seventh :minor :ninth :major}])

(defonce state (r/atom {:editing true :value "Am"}))
(defcard-rg try-out-box
  "Enter raw chords here to test out the chord parser"
  (fn []
    (if (:editing @state)
      [editable-chord {:text (:value @state) :on-blur #(reset! state {:editing false :value %})}]
      [displayed-chord (assoc (parse (:value @state))
                              :on-click #(swap! state assoc :editing true))]))
  state)
