(ns frontend.reducer
  (:require [clojure.zip :as zip]
            [datascript.core :as d]
            [frontend.models.sheet-2 :as sheet-2]
            [frontend.router :as router]
            [frontend.util.util :refer [combine-reducers]]
            [shared.datsync :as datsync]
            [shared.utils :as sutil :refer [key-by]]))

(defn selection [state [type arg1 arg2]]
  (case type
    :app/init nil
    :sheet/deselect nil
    :sheet/select {:selection/type arg1 :selection/id arg2}
    :sheet/move {:selection/type :selection/chord :selection/id arg1}
    :response/datsync (if-let [chord-id (get (:tempids arg1) "new-chord")]
                        {:selection/type :selection/chord :selection/id chord-id}
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

(defn- transact! [db tx-data]
  (:db-after (d/with db tx-data)))

(defn sheets-datascript [db [type arg1 arg2]]
  (case type
    :app/init @(d/create-conn sheet-2/schema)
    :response/get-sheet (transact! db [arg1])
    :tx/apply (:db-after arg1)
    :response/datsync (transact! db (datsync/datoms->tx (:tx-data arg1)))
    db))

(def app
  (combine-reducers
    {:db/sheets-datascript sheets-datascript
     :db/selection selection
     :db/current-user current-user
     :db/active-route active-route}))
