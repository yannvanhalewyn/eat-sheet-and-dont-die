(ns sheet-bucket.controllers.sheets
  (:require [sheet-bucket.models.sheet :as sheet]
            [datomic.api :as d]
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

(defn create [{:keys [db-conn params]}]
  (let [res (d/transact db-conn
              [{:db/id "new-sheet" :sheet/title "Title" :sheet/artist "Artist"}
               {:db/id (:owner-id params) :playlist/sheets "new-sheet"}])]
    (try
      (response
        (d/pull (:db-after @res) '[*] (get-in @res [:tempids "new-sheet"])))
      (catch Exception e
        (status (response {:error (.getMessage e)}) 500)))))

(defn update [{:keys [db-conn params] :as req}]
  (let [result (d/transact db-conn (sheet/diff->tx (:tx params) (Long. (:eid params))))]
    (try
      (response {:temp-ids (:tempids @result)})
      (catch Exception e
        (status (response {:error (.getMessage e)}) 500)))))

(defn destroy [{:keys [db-conn params] :as req}]
  (let [result (d/transact db-conn [[:db.fn/retractEntity (Long. (:eid params))]])]
    (try
      (response {:success true :removed-id (Long. (:eid params))})
      (catch Exception e
        (status (response {:error (.getMessage e)}) 500)))))
