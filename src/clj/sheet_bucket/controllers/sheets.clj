(ns sheet-bucket.controllers.sheets
  (:require [datomic.api :as d]
            [clojure.walk :refer [postwalk]]
            [ring.util.response :refer [response status]]))

(defn index [{:keys [db-conn params]}]
  (response
    (flatten (d/q '[:find (pull ?sheet [:db/id :sheet/artist :sheet/title])
                    :in $ ?user
                    :where [?user :playlist/sheets ?sheet]]
               (d/db db-conn)
               (Long. (:user-id params))))))

(defn- sort-children [root]
  (postwalk
    (fn [node]
      (if (and (sequential? node) (map? (first node)))
        (sort-by :coll/position node)
        node))
    root))

(defn show [{:keys [db-conn params]}]
  (response (sort-children (d/pull (d/db db-conn) '[*] (Long. (:eid params))))))

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
    (try
      (response {:temp-ids (:tempids @result)})
      (catch Exception e
        (status (response {:error (.getMessage e)}) 500)))))
