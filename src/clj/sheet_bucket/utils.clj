(ns sheet-bucket.utils
  (:require [clojure.string :as str]))

(defn keywordize
  "Will return any string as a convenional keyword, e.g: \"FOO_BAR\" => :foo-bar"
  [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn parse-int
  "Safely attempts to extract an integer from a string. Will return
  the first found integer, without decimals. nil if none found."
  [s]
  (if-let [x (re-find #"-?\d+" (str s))]
    (Integer. x)))
