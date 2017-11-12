(ns cards.section
  (:require [frontend.views.section :as subject]
            [frontend.util.util :refer [gen]]
            [shared.specs :as specs])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]
                   [cards.core :refer [defcard-props]]))

(defonce section (first (gen ::specs/section 1)))
(def selected (-> section :section/rows first :row/bars second :bars/chords first :db/id))

(defcard-doc
  "# Section"
  "Section has rows of bars bla bla"
  "## Sample props"
  {:section section
   :on-chord-click 'click-fn
   :on-chord-update 'update-fn})

(defcard-props base
  "Should launch an alert with the chord ID on click"
  [subject/component
   {:section section :on-chord-click js/alert}])

(defn on-chord-update [id new-val] (.log js/console (str "Update - id: " id ", value: " new-val)))
(defcard-props with-current-chord
  "Should display the first chord of the second bar as editable (if
  there). Should log out id and new value in console."
  [subject/component {:section section
                      :selected selected
                      :on-chord-update on-chord-update }])
