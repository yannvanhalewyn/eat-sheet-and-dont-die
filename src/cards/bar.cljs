(ns cards.bar
  (:require [reagent.core :as reagent]
            [sheet-bucket.components.bar :as bar])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-doc]]))

(defn- bar [props]
  (reagent/as-element (bar/component props)))

(defcard "**ExampleProps**" {:chords [{:root "a"} {:root "c" :triad :minor}]})

(defcard SingleChord (bar {:chords [{:root "a"}]}))
(defcard TwoChords (bar {:chords [{:root "a"} {:root "c" :triad :minor}]}))
(defcard ThreeChords (bar {:chords [{:root "a"} {:root "c" :triad :minor} {:root "c" :triad :minor}]}))
(defcard FourChords (bar {:chords [{:root "a"}
                                   {:root "c" :triad :minor}
                                   {:root "E"}
                                   {:root "c" :triad :minor}]}))
