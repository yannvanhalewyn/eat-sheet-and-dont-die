(ns sheet-bucket.controllers.sheets
  (:require [sheet-bucket.models.sheet :as sheet]
            [datomic.api :as d]
            [clojure.walk :refer [postwalk]]))

(defn update [db-conn {:keys [sheet-id diff]}]
  (let [result (d/transact db-conn (sheet/diff->tx diff sheet-id))]
    (try
      {:temp-ids (:tempids @result) :sheet-id sheet-id}
      (catch Exception e
        {:error (.getMessage e)}))))

(defn destroy [db-conn sheet-id]
  (let [result (d/transact db-conn [[:db.fn/retractEntity sheet-id]])]
    (try
      {:success true :removed-id sheet-id}
      (catch Exception e
        {:error (.getMessage e)}))))
