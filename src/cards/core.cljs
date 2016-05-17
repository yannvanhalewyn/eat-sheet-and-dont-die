(ns cards.core
  (:require [reagent.core :as reagent])
  (:require-macros [devcards.core :as dc :refer [defcard deftest]]))

(defn component [state]
  [:div
   [:h1 (str "Hello! " state)]])

(defcard test-card
  (dc/reagent (component "foo")))
