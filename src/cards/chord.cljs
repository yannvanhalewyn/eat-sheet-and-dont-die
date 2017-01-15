(ns cards.chord
  (:require [cards.util :refer [alert]]
            [sheet-bucket.models.chord :refer [parse gen]]
            [sheet-bucket.components.chord :refer [displayed-chord editable-chord]]
            [devcards.core :refer [markdown->react]]
            [reagent.core :as r])
  (:require-macros [devcards.core :refer [defcard-doc defcard-rg]]
                   [sheet-bucket.util.util :refer [fori]]
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
  when it's not editing the chord."
  "### props"
  (first (gen 1)))

(def roots ["A" "B" "C" "D" "E" "F" "G" "1" "2" "3" "4" "5" "6" "7"])

(defcard-props Major
  "Clicking the chord should launch an alert"
  [displayed-chord {:root ["A" :flat] :on-click (alert "click")}])

(defn render-multi [chords]
  [:div {:style {:display :flex :justify-content :space-between}}
   (fori [i chord chords]
     ^{:key i} [displayed-chord chord])])

(defn for-all-roots [attrs title]
  [:div
   (if title [:h2 {:style {:color "#333"}} title])
   [markdown->react (merge {:root ['root]} attrs)]
   [render-multi (for [root roots] (merge {:root [root]} attrs))]])

(defcard-rg roots
  "All the natural roots"
  [render-multi (map (fn [root] {:root [root]}) roots)])

(defcard-rg flats
  "All the flat roots"
  [render-multi (map (fn [root] {:root [root :flat]}) roots)])

(defcard-rg sharps
  "All the sharp roots"
  [render-multi (map (fn [root] {:root [root :sharp]}) roots)])

;; Triads
(defcard-rg Triads
  [:div
   [for-all-roots {:triad :minor} "Minor triads"]
   [for-all-roots {:triad :augmented} "Augmented triads"]])

;; Sevenths
(defcard-rg Sevenths
  [:div
   [for-all-roots {:seventh :minor} "Dominant seventh"]
   [for-all-roots {:seventh :major} "Major seventh"]
   [for-all-roots {:triad :minor :seventh :minor} "Minor sevenths"]
   [for-all-roots {:triad :minor :seventh :major} "Minor major sevenths"]])

;; Ninths
(defcard-rg Ninths
  [:div
   [for-all-roots {:seventh :minor :ninth :natural} "Dominant ninths"]
   [for-all-roots {:seventh :major :ninth :natural} "Major ninths"]
   [for-all-roots {:triad :minor :seventh :minor :ninth :natural} "Minor ninths"]])

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
