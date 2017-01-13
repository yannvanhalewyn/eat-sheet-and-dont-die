(ns cards.section
  (:require [sheet-bucket.components.section :as subject]
            [sheet-bucket.models.chord :refer [gen-row]])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]
                   [cards.core :refer [defcard-props]]))

(defonce rows (gen-row 6))

(defcard-doc
  "# Section"
  "Section has rows of bars bla bla"
  "## Sample props"
  {:name "Section Name"
   :rows (cons (take 1 rows) [["..."]])
   :on-chord-click 'click-fn
   :on-chord-update 'update-fn
   :selected (-> rows first second first)})

(defcard-props base
  "Should launch an alert with the chord ID on click"
  [subject/component
   {:name "Intro" :rows rows :on-chord-click js/alert}])

(def on-chord-update #(js/alert (str %1 " with " %2)))
(defcard-props with-current-chord
  "Should display the first chord of the second bar as editable (if there)"
  [subject/component {:name "Intro"
                      :rows rows
                      :selected (-> rows first second first :id)
                      :on-chord-update on-chord-update}])
