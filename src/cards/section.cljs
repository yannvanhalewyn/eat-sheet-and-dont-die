(ns cards.section
  (:require [cards.util :refer [unparse-rows]]
            [sheet-bucket.components.section :as subject]
            [sheet-bucket.util.util :refer [gen]]
            [sheet-bucket.specs.editor :as specs])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]
                   [cards.core :refer [defcard-props]]))

(defonce section
  (-> (first (gen ::specs/section 1))
      (update :rows unparse-rows)))

(def selected (-> section :rows first second first :id))

(defcard-doc
  "# Section"
  "Section has rows of bars bla bla"
  "## Sample props"
  (assoc section
         :on-chord-click 'click-fn
         :on-chord-update 'update-fn
         :selected selected))

(defcard-props base
  "Should launch an alert with the chord ID on click"
  [subject/component
   (assoc section :on-chord-click js/alert)])

(defn on-chord-update [id new-val] (.log js/console (str "Update - id: " id ", value: " new-val)))
(defcard-props with-current-chord
  "Should display the first chord of the second bar as editable (if
  there). Should log out id and new value in console."
  [subject/component (assoc section
                            :selected selected
                            :on-chord-update on-chord-update)])
