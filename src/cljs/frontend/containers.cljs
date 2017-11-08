(ns frontend.containers
  (:require [frontend.views.layout.application :as application]
            [re-frame.core :refer [subscribe]]))

(def app application/component)
