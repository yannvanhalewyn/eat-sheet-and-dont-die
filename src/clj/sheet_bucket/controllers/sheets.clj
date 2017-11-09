(ns sheet-bucket.controllers.sheets
  (:require  [clojure.core.async :refer [<!!]]
             [datomic.client :as client]
             [ring.util.response :refer [response]]))

(defn- find-one [conn]
  (ffirst (<!! (client/q conn
                         {:query '[:find ?sheet
                                   :where [?sheet :sheet/title]]
                          :args [(client/db conn)]}))))

(defn index [{:keys [db-conn]}]
  (response (<!! (client/pull (client/db db-conn)
                              {:eid (find-one db-conn)
                               :selector '[*]}))))

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
  (let [result (<!! (client/transact db-conn
                      {:tx-data (map #(->tx % (Long. (:eid params))) (:tx params))}))]
    (response {:temp-ids (:tempds result)})))
