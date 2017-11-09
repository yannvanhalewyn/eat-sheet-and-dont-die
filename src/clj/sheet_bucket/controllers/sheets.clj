(ns sheet-bucket.controllers.sheets
  (:require [datomic.api :as d]
            [ring.util.response :refer [response]]))

(defn- find-one [conn]
  (ffirst (d/q conn
            '[:find ?sheet :where [?sheet :sheet/title]]
            (d/db conn))))

(defn index [{:keys [db-conn]}]
  (response (d/pull (d/db db-conn) '[*] (find-one db-conn))))

(defn ->tx [tx sheet-id]
  (if-let [retract (:removed tx)]
    [:db.fn/retractEntity (:db/id retract)]
    (if-let [new-entity (or (:added tx) (:new-value tx))]
      (reduce
        (fn [children [id ref-key]]
          {:db/id id
           ref-key children})
        new-entity
        (reverse (partition-all 2
                   (cons sheet-id (:path tx))))))))

(defn update [{:keys [db-conn params] :as req}]
  (let [result (d/transact db-conn
                 (map #(->tx % (Long. (:eid params))) (:tx params)))]
    (response {:temp-ids (:tempids result)})))
