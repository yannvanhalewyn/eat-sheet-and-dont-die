(ns frontend.core
  (:require [reagent.core :as reagent]
            [redux.core :as redux]
            [goog.dom :as gdom]
            [redux.middleware :as middleware]
            [frontend.reducer :as reducer]
            [frontend.containers :as containers]
            [devtools.core :as devtools]))

(devtools/install! [:custom-formatters :sanity-hints])

(defn start
  "Bootstraps the app and returns a render fn"
  []
  (let [state (reagent/atom {})
        render #(reagent/render [containers/app state] (gdom/getElement "app"))]
    (redux/start state reducer/app [middleware/wrap-logger])
    (render)
    render))

(defonce render (start))
