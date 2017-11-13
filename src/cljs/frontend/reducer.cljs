(ns frontend.reducer
  (:require [frontend.util.util :refer [combine-reducers]]
            [clojure.zip :as zip]))


(defn sheets-by-id [state [type arg1 arg2]]
  (case type
    :app/init {}
    :sheet/set-artist (assoc-in state [arg1 :sheet/artist] arg2)
    :sheet/set-title (assoc-in state [arg1 :sheet/title] arg2)
    :sheet/replace (assoc state (:db/id arg1) arg1)
    :sheet/replace-zip (let [sheet (zip/root arg1)]
                         (assoc state (:db/id sheet) sheet))
    state))


(defn selected-chord [state [type arg]]
  (case type
    :app/init nil
    :sheet/deselect nil
    :sheet/select-chord arg
    :sheet/replace-zip (:db/id (zip/node arg))
    state))

(defn current-user [state [type]]
  (case type
    :app/init nil
    state))

(defn active-route [state [type new-route]]
  (case type
    :app/init {:route/handler :route/index}
    :route/browser-url new-route
    state))

(def app
  (combine-reducers
    {:db/sheets.by-id sheets-by-id
     :db/selected selected-chord
     :db/current-user current-user
     :db/active-route active-route}))
