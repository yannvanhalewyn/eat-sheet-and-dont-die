(ns frontend.util.regex
  (:refer-clojure :exclude [or])
  (:require [clojure.string :as str]
            [goog.string :refer [format]]))

(defn or [& opts]
  (str/join "|" opts))

(defn multi [body]
  (str body "*"))

(defn maybe [body]
  (str body "?"))

(defn group [& body]
  (format "(%s)" (str/join body)))

(defn non-capturing-group [& body]
  (group (str "?:" (str/join body))))

(def maybe-group (comp maybe group))
(def maybe-group-non-capturing (comp maybe non-capturing-group))

(defn neg-lookahead [body]
  (format "(?!%s)" body))

(defn build [& patterns]
  (re-pattern (str/join patterns)))
