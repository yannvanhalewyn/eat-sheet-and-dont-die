(ns frontend.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [goog.dom :as gdom]
            [frontend.containers :as containers]
            [frontend.events]
            [frontend.subs]
            [devtools.core :as devtools]))

(devtools/install! [:custom-formatters :sanity-hints])

(defn start
  "Bootstraps the app and returns a render fn"
  []
  (let [render #(reagent/render containers/app (gdom/getElement "app"))]
    (rf/dispatch-sync [:event/init])
    (render)
    render))

(defonce render (start))
