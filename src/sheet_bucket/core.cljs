(ns sheet-bucket.core
  (:require [reagent.core :as reagent]
            [redux.core :as redux]
            [redux.utils :refer [create-container]]
            [devtools.core :as devtools]
            [goog.dom :as gdom]
            [redux.middleware :as middleware]))

(devtools/install! [:custom-formatters :sanity-hints])

(defn action [state]
  (redux/transact! state {:type :foo}))

(defn main [props]
  [:button {:on-click (:on-click props)} "Click"])

(def app-container
  (create-container
   :component main
   :selectors {:state identity}
   :actions {:on-click action}))

(defn start
  "Bootstraps the app and returns a render fn"
  []
  (let [state (reagent/atom {})
        render #(reagent/render [app-container state] (gdom/getElement "app"))]
    (redux/start state identity [middleware/wrap-logger])
    (render)
    render))

(defonce render (start))
