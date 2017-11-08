(ns sheet-bucket.containers
  (:require [sheet-bucket.components.layout.application :as application]
            [sheet-bucket.selectors :as selectors]
            [sheet-bucket.actions :refer [select-chord update-chord append delete deselect move]]
            [redux.utils :refer [create-container]]))

(def app
  (create-container
    :component application/component
    :selectors {:sheet selectors/sheet
                :selected selectors/selected}
    :actions {:update-chord update-chord
              :on-chord-click select-chord
              :deselect deselect
              :append append
              :move move
              :remove delete}))
