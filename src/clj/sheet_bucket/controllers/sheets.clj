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
