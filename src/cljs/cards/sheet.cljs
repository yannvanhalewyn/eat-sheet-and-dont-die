(ns cards.sheet
  (:require [cards.util :refer [unparse-sheet]]
            [frontend.views.sheet :as subject]
            [frontend.util.util :refer [gen]]
            [frontend.specs.editor :as specs]
            [re-frame.core :refer [reg-sub reg-event-db]])
  (:require-macros [devcards.core :refer [defcard-rg defcard-doc]]))

(defonce sheet (unparse-sheet (first (gen ::specs/sheet 1))))
(def selected (-> sheet :sheet/sections first :section/rows first :row/bars first :bar/chords second :db/id))

(reg-sub :sub/sheet (constantly sheet))
(reg-sub :sub/selected (constantly selected))

(def props
  {:sheet sheet
   :on-chord-click js/alert
   :on-chord-update (.-log js/console)
   :selected selected})

(defcard-doc
  "# Sheet"
  "## Example sheet data"
  "The sheet component subscribes in re-frame to the `:subs/sheet`
  subscription. Here is some generated sample data that could be
  returned:"
  sheet)

(defcard-rg base
  "The second chord of the first bar of the first row of the first
  section should be editable if present."
  [subject/component props])
