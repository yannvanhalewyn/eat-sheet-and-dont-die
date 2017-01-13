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
   :rows rows})

(defcard-props base
  [subject/component {:name "Intro" :rows rows}])
