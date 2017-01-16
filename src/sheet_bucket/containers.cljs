(ns sheet-bucket.containers
  (:require [sheet-bucket.components.sheet :as sheet]
            [sheet-bucket.selectors :refer [sections attributes selected]]
            [sheet-bucket.actions :refer [select-chord update-chord]]
            [redux.utils :refer [create-container]]))

(def app
  (create-container
   :component sheet/component
   :selectors {:sections sections
               :attrs attributes
               :selected selected}
   :actions {:on-chord-update update-chord
             :on-chord-click select-chord}))
