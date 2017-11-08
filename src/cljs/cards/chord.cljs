(ns cards.chord
  (:require [cards.util :refer [alert]]
            [frontend.models.chord :refer [parse]]
            [frontend.specs.editor :as specs]
            [frontend.util.util :refer [gen]]
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
   [for-all-roots {:chord/seventh :minor} "Dominant seventh"]
   [for-all-roots {:chord/seventh :major} "Major seventh"]
   [for-all-roots {:chord/triad :minor :chord/seventh :minor} "Minor sevenths"]
   [for-all-roots {:chord/triad :minor :chord/seventh :major} "Minor major sevenths"]
   [for-all-roots {:chord/triad :diminished :chord/seventh :minor} "Half diminished"]
   [for-all-roots {:chord/triad :diminished :chord/seventh :diminished} "Diminished"]])

;; Ninths
(defcard-rg Ninths
  [:div
   [for-all-roots {:chord/seventh :minor :chord/ninth :natural} "Dominant ninths"]
   [for-all-roots {:chord/seventh :major :chord/ninth :natural} "Major ninths"]
   [for-all-roots {:chord/triad :minor :chord/seventh :minor :chord/ninth :natural} "Minor ninths"]
   [for-all-roots {:chord/ninth :sharp} "Sharp ninths"]
   [for-all-roots {:chord/ninth :flat} "Flat ninths"]])

(defcard-rg Altered
  [:div
   [for-all-roots {:chord/seventh :minor :chord/ninth :flat} "Dominant flat 9"]
   [for-all-roots {:chord/seventh :minor :chord/ninth :sharp} "Dominant sharp 9"]])

(defcard-props No-root
  "Should not display anything"
  [displayed-chord {:chord {:chord/root nil
                            :chord/triad :major
                            :chord/seventh :minor
                            :chord/ninth :major}}])
