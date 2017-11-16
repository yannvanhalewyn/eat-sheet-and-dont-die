(ns frontend.reducer
  (:require [frontend.util.util :refer [combine-reducers]]
            [shared.utils :as sutil :refer [key-by]]
            [clojure.zip :as zip]
            [frontend.router :as router]
            [frontend.models.sheet :as sheet]))

(defn- handle-response [state])

(defn sheets-by-id [state [type arg1 arg2]]
  (case type
    :app/init {}
    :sheet/set-artist (assoc-in state [arg1 :sheet/artist] arg2)
    :sheet/set-title (assoc-in state [arg1 :sheet/title] arg2)
    :sheet/replace (assoc state (:db/id arg1) arg1)
    :sheet/replace-zip (let [sheet (zip/root arg1)]
                         (assoc state (:db/id sheet) sheet))
    (:response/get-sheet :response/create-sheet) (assoc state (:db/id arg1) arg1)
    :response/get-sheets (key-by :db/id arg1)
    :response/destroy-sheet (dissoc state (:removed-id arg1))
    :response/sync-sheet
    (update state (:sheet-id arg1) sutil/replace-temp-ids (:temp-ids arg1))
    state))

(defn selection [state [type arg]]
  (case type
    :app/init nil
    :sheet/deselect nil
    :sheet/select arg
    :sheet/replace-zip (:db/id (zip/node arg))
    :response/create-sheet (:db/id (sheet/first-chord arg))
    :response/sync-sheet (if-let [new-id (get-in arg [:temp-ids state])]
                           new-id
                           state)
    state))

(defn current-user [state [type arg1]]
  (case type
    :app/init nil
    :response/get-current-user arg1
    state))

(defn active-route [state [type arg1]]
  (case type
    :app/init {:route/handler :route/index}
    :route/browser-url arg1
    :response/create-sheet (router/sheet (:db/id arg1))
    state))

(def app
  (combine-reducers
    {:db/sheets.by-id sheets-by-id
     :db/selection selection
     :db/current-user current-user
     :db/active-route active-route}))
