(ns sheet-bucket.core
  (:require [reagent.core :as reagent]
            [redux.core :as redux]
            [goog.dom :as gdom]
            [redux.middleware :as middleware]

            [devtools.core :as devtools]
            [redux.utils :refer [create-container]]
            [sheet-bucket.components.section :as section]
            [sheet-bucket.models.chord :refer [parse]]
            [clojure.zip :as zip]))

(devtools/install! [:custom-formatters :sanity-hints])

;; Selector
(def section-name :name)
(def selected :current)
(def rows-raw :rows)

(defn rows [state]
  (for [row (rows-raw state)]
    (for [bar row]
      (map #(merge % (parse (:raw %))) bar))))

;; Actions
(defn update-chord [state _ value]
  (let [root (zip/vector-zip (rows-raw @state))]
    (loop [loc root]
      (if (= (:id (zip/node loc)) (selected @state))
        (redux/transact! state {:type :update-rows
                                :value (zip/root (zip/edit loc assoc :raw value))})
        (recur (zip/next loc))))))

(defn select-chord [state id]
  (redux/transact! state {:type :select-chord :id id}))

;; Reducer
(defn reducer [state action]
  (case (:type action)
    :init {:name "Intro" :rows [[[{:id "1" :raw "am"} {:id "2" :raw "edit-me"}]]] :current "2"}
    :select-chord (assoc state :current (:id action))
    :update-rows (assoc state :rows (:value action) :current nil)
    state))

;; Container
(def app-container
  (create-container
   :component section/component
   :selectors {:name section-name
               :rows rows
               :selected selected}
   :actions {:on-chord-update update-chord
             :on-chord-click select-chord}))

(defn start
  "Bootstraps the app and returns a render fn"
  []
  (let [state (reagent/atom {})
        render #(reagent/render [app-container state] (gdom/getElement "app"))]
    (redux/start state reducer [middleware/wrap-logger])
    (render)
    render))

(defonce render (start))
