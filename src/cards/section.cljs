(ns cards.section
  (:require [cards.util :refer [unparse-section]]
            [sheet-bucket.components.section :as subject]
            [sheet-bucket.util.util :refer [gen]]
            [sheet-bucket.specs.editor :as specs])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]
                   [cards.core :refer [defcard-props]]))

(defonce section (first (gen ::specs/section 1)))
(def props {:rows (first (unparse-section section))
            :attrs (second section)})

(def children first)
(def selected (-> section children first children second children first :id))

(defcard-doc
  "# Section"
  "Section has rows of bars bla bla"
  "## Sample props"
  (assoc props
         :on-chord-click 'click-fn
         :on-chord-update 'update-fn))

(defcard-props base
  "Should launch an alert with the chord ID on click"
  [subject/component
   (assoc props :on-chord-click js/alert)])

(defn on-chord-update [id new-val] (.log js/console (str "Update - id: " id ", value: " new-val)))
(defcard-props with-current-chord
  "Should display the first chord of the second bar as editable (if
  there). Should log out id and new value in console."
  [subject/component (assoc props
                            :selected selected
                            :on-chord-update on-chord-update)])
