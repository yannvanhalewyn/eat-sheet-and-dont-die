(ns sheet-bucket.containers
  (:require [sheet-bucket.components.section :as section]
            [sheet-bucket.selectors :refer [section-name rows selected]]
            [sheet-bucket.actions :refer [select-chord update-chord]]
            [redux.utils :refer [create-container]]))

(def app
  (create-container
   :component section/component
   :selectors {:name section-name
               :rows rows
               :selected selected}
   :actions {:on-chord-update update-chord
             :on-chord-click select-chord}))
