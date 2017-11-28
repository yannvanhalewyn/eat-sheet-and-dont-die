(ns frontend.reducer
  (:require [clojure.zip :as zip]
            [datascript.core :as d]
            [frontend.models.sheet :as sheet]
            [frontend.models.sheet-zip :as sheet-zip]
            [frontend.router :as router]
            [frontend.util.util :refer [combine-reducers]]
            [shared.datsync :as datsync]
            [shared.utils :as sutil :refer [key-by]]))

(defn- make-selection [type id]
  {:selection/type type :selection/id id})

(def chord-selection (partial make-selection :selection/chord))

(defn selection [state [type arg1 arg2]]
  (case type
    :app/init nil
    :sheet/deselect nil
    :sheet/select (make-selection arg1 arg2 )
    :sheet/move (chord-selection arg1)
    :response/datsync (if-let [chord-id (get (:tempids arg1) "new-chord")]
                        (chord-selection chord-id)
                        state)
    :response/create-sheet (-> arg1 sheet-zip/zipper
                             sheet-zip/nearest-chord zip/node :db/id
                             chord-selection)
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

(defn- transact! [db tx-data]
  (:db-after (d/with db tx-data)))

(defn sheets [db [type arg1 arg2]]
  (case type
    :app/init @(d/create-conn sheet/schema)
    :response/get-sheet (transact! db [arg1])
    :response/create-sheet (transact! db [arg1])
    :response/datsync
    (transact! db (datsync/datoms->tx (:tx-data arg1)))
    :chsk/incoming-tx-data
    (transact! db (datsync/datoms->tx arg1))
    :response/get-sheets (transact! db arg1)
    db))

(defn modal [state [type key props]]
  (case type
    (:app/init :modal/close) nil
    :modal/show {:modal/key key :modal/props props}
    state))

(def app
  (combine-reducers
    {:db/sheets sheets
     :db/selection selection
     :db/current-user current-user
     :db/modal modal
     :db/active-route active-route}))
