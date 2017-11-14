(ns cards.core
  (:require [reagent.core :as reagent]
            [devtools.core :as devtools]
            [cards.chord]
            [cards.section]
            [cards.bar]
            [cards.sheet]))

(defonce run-once
  (do
    (enable-console-print!)
    (devtools/install! [:custom-formatters :sanity-hints])))
