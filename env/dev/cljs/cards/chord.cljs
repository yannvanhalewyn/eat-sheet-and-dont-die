(ns cards.chord
  (:require [cards.util :refer [alert gen]]
            [frontend.models.chord :refer [parse]]
            [shared.specs :as specs]
            [frontend.views.chord :refer [displayed-chord editable-chord]]
            [devcards.core :refer [markdown->react]]
            [reagent.core :as r])
  (:require-macros [devcards.core :refer [defcard-doc defcard-rg]]
                   [shared.utils :refer [fori]]
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
  [editable-chord {:chord {:chord/value "a-maj"}
                   :update-chord (.-log js/console)
                   :append (partial (.-log js/console) "ADD ")
                   :remove (partial (.-log js/console) "REMOVE ")
                   :move (partial (.-log js/console) "MOVE ")
                   :deselect #(.log js/console "DESELECT")}])

(defcard-doc
  "## Display Chord"
  "This is a chord in it's rest state. This is what the user will see
  when it's not editing the chord."
  "### props"
  (first (gen ::specs/chord 1)))

(def roots ["A" "B" "C" "D" "E" "F" "G" "1" "2" "3" "4" "5" "6" "7"])

(defcard-props Major
  "Clicking the chord should launch an alert"
  [displayed-chord {:chord {:chord/root ["A" :flat]} :on-click (alert "click")}])

(defn render-multi [chords]
  [:div {:style {:display :flex :justify-content :space-between}}
   (fori [i chord chords]
     ^{:key i} [displayed-chord {:chord chord}])])

(defn for-all-roots [attrs title]
  [:div
   (if title [:h2 {:style {:color "#333"}} title])
   [markdown->react {:chord (assoc attrs :chord/root ['root])}]
   [render-multi (for [root roots] (assoc attrs :chord/root [root]))]])

(defcard-rg roots
  "All the natural roots"
  [render-multi (map (fn [root] {:chord/root [root]}) roots)])

(defcard-rg flats
  "All the flat roots"
  [render-multi (map (fn [root] {:chord/root [root :flat]}) roots)])

(defcard-rg sharps
  "All the sharp roots"
  [render-multi (map (fn [root] {:chord/root [root :sharp]}) roots)])

;; Triads
(defcard-rg Triads
  [:div
   [for-all-roots {:chord/triad :minor} "Minor triads"]
   [for-all-roots {:chord/triad :augmented} "Augmented triads"]
   [for-all-roots {:chord/triad :diminished} "Diminished triads"]])

;; Sevenths
(defcard-rg Sevenths
  [:div
   [for-all-roots {:chord/seventh :flat} "Dominant seventh"]
   [for-all-roots {:chord/seventh :natural} "Major seventh"]
   [for-all-roots {:chord/triad :minor :chord/seventh :flat} "Minor sevenths"]
   [for-all-roots {:chord/triad :minor :chord/seventh :natural} "Minor major sevenths"]
   [for-all-roots {:chord/triad :diminished :chord/seventh :flat} "Half diminished"]
   [for-all-roots {:chord/triad :diminished} "Diminished"]])

;; Ninths
(defcard-rg Ninths
  [:div
   [for-all-roots {:chord/seventh :flat :chord/extensions [["9" :natural]]} "Dominant ninths"]
   [for-all-roots {:chord/seventh :natural :chord/extensions [["9" :natural]]} "Major ninths"]
   [for-all-roots {:chord/triad :minor :chord/seventh :flat :chord/extensions [["9" :natural]]} "Minor ninths"]
   [for-all-roots {:chord/extensions [["9" :sharp]]} "Sharp ninths"]
   [for-all-roots {:chord/extensions [["9" :flat]]} "Flat ninths"]])

(defcard-rg Altered
  [:div
   [for-all-roots {:chord/seventh :flat :chord/extensions [["9" :flat]]} "Dominant flat 9"]
   [for-all-roots {:chord/seventh :flat :chord/extensions [["9" :sharp]]} "Dominant sharp 9"]])

(defcard-rg Other-extensions
  [:div
   [for-all-roots {:chord/seventh :natural :chord/extensions [["11" :sharp]]} "Major #11"]
   [for-all-roots {:chord/seventh :flat :chord/extensions [["9" :natural] ["11" :sharp] ["5" :flat]]}
    "Dominant 9 #11 b5"]])

(defcard-props No-root
  "Should not display anything"
  [displayed-chord {:chord {:chord/root nil
                            :chord/triad :major
                            :chord/seventh :flat}}])
