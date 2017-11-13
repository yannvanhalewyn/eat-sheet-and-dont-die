(ns frontend.core
  (:require [frontend.router :as router]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [goog.dom :as gdom]
            [goog.string.format]
            [frontend.containers :as containers]
            [frontend.events]
            [frontend.subs]))

(defn render! []
  (reagent/render containers/app (gdom/getElement "app")))

(defn init! []
  (rf/dispatch-sync [:app/init])
  (router/listen)
  (render!))
