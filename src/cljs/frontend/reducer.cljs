(ns frontend.reducer
  (:require [clojure.zip :as zip]
            [datascript.core :as d]
            [frontend.models.sheet :as sheet]
            [frontend.models.sheet-2 :as sheet-2]
            [frontend.router :as router]
            [frontend.util.util :refer [combine-reducers]]
            [shared.datsync :as datsync]
            [shared.utils :as sutil :refer [key-by]]))

(defn- handle-response [state])

(defn sheets-by-id [state [type arg1 arg2]]
  (case type
    :app/init {}
    :sheet/set-artist (assoc-in state [arg1 :sheet/artist] arg2)
    :sheet/set-title (assoc-in state [arg1 :sheet/title] arg2)
    ;; :sheet/replace (assoc state (:db/id arg1) arg1)
    ;; :sheet/replace-zip (let [sheet (zip/root arg1)]
    ;;                      (assoc state (:db/id sheet) sheet))
    (:response/get-sheet :response/create-sheet) {}
    :response/get-sheets (key-by :db/id arg1)
    :response/destroy-sheet (dissoc state (:removed-id arg1))
    :response/sync-sheet
    (update state (:sheet-id arg1) sutil/replace-temp-ids (:temp-ids arg1))
    state))

(defn v [[e a v t added]] v)

(defn selection [state [type arg1 arg2]]
  (case type
    :app/init nil
    :sheet/deselect nil
    :sheet/select {:selection/type arg1 :selection/id arg2}
    :sheet/replace-zip {:selection/type :selection/chord
                        :selection/id (:db/id (zip/node arg1))}
    :response/create-sheet {:selection/type :selection/chord
                            :selection/id (:db/id (sheet/first-chord arg1))}
    :response/sync-sheet (if-let [new-id (get-in arg1 [:temp-ids (:selection/id state)])]
                           (assoc state :selection/id new-id)
                           state)
    :response/datsync (if-let [chord-id (->> (:tx-data arg1)
                                          (filter #(= :bar/chords (second %)))
                                          last v)]
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
    {:db/sheets.by-id sheets-by-id
     :db/sheets-datascript sheets-datascript
     :db/selection selection
     :db/current-user current-user
     :db/active-route active-route}))
