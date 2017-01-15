(ns cards.section
  (:require [cards.util :refer [unparse-chord]]
            [sheet-bucket.components.section :as subject]
            [sheet-bucket.models.chord :refer [gen-row]])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]
                   [cards.core :refer [defcard-props]]))

(defonce rows
  (for [row (gen-row 6)]
    (for [bar row] (map unparse-chord bar))))

(defcard-doc
  "# Section"
  "Section has rows of bars bla bla"
  "## Sample props"
  {:name "Section Name"
   :rows rows
   :on-chord-click 'click-fn
   :on-chord-update 'update-fn
   :selected (-> rows first second first :id)})

(defcard-props base
  "Should launch an alert with the chord ID on click"
  [subject/component
   {:name "Intro" :rows rows :on-chord-click js/alert}])

(defn on-chord-update [id new-val] (.log js/console (str "Update - id: " id ", value: " new-val)))
(defcard-props with-current-chord
  "Should display the first chord of the second bar as editable (if
  there). Should log out id and new value in console."
  [subject/component {:name "Intro"
                      :rows rows
                      :selected (-> rows first second first :id)
                      :on-chord-update on-chord-update}])
