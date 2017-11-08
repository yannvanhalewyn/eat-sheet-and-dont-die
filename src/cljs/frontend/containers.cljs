(ns frontend.containers
  (:require [frontend.components.layout.application :as application]
            [re-frame.core :refer [subscribe]]))

(def app application/component)
