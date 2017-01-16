(ns cards.sheet
  (:require [cards.util :refer [unparse-section]]
            [sheet-bucket.components.sheet :as subject]
            [sheet-bucket.util.util :refer [gen]]
            [sheet-bucket.specs.editor :as specs])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]))

(defonce sheet (first (gen ::specs/sheet 1)))

(def children first)
(def selected (-> sheet children first children first children first children second :id))
(def props
  {:attrs (second sheet)
   :on-chord-click js/alert
   :on-chord-update (.-log js/console)
   :selected selected
   :sections (map unparse-section (first sheet))})

(defcard-doc
  "# Sheet"
  "## Example props"
  (assoc props :sections 'list-of-sections))

(defcard-rg base
  "The second chord of the first bar of the first row of the first
  section should be editable if present."
  [subject/component props])
