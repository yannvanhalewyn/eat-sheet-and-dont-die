(ns cards.sheet
  (:require [cards.util :refer [unparse-sections]]
            [sheet-bucket.components.sheet :as subject]
            [sheet-bucket.util.util :refer [gen]]
            [sheet-bucket.specs.editor :as specs])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]))

(defonce sheet
  (update (first (gen ::specs/sheet 1))
          :sections unparse-sections))

(def selected (-> sheet :sections first :rows second first second :id))
(def props
  (assoc sheet
         :on-chord-click js/alert
         :on-chord-update (.-log js/console)
         :selected selected))

(defcard-doc
  "# Sheet"
  "## Example props"
  (assoc props :sections 'list-of-sections))

(defcard-rg base
  "The second chord of the first bar of the first section should be editable if present."
  [subject/component props])
