(ns sheet-bucket.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [sheet-bucket.utils :refer [keywordize]]))

(defn read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn read-env-file []
  (if-let [env-file (io/file ".lein-env")]
    (if (.exists env-file)
      (edn/read-string (slurp env-file)))))

(defonce config
  (merge
    (read-env-file)
    (read-system-env)))
