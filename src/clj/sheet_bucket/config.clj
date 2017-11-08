(ns sheet-bucket.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [sheet-bucket.utils :refer [keywordize parse-int]]))

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
