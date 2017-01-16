(ns sheet-bucket.models.sheet
  (:require [clojure.zip :as zip :refer [end? next node]]))

(def new-sheet
  [[;; Sections
    [[;; Rows
      [[;; Bars
        [[{:id "1" :raw "edit-me"}]]]]]
     {:name "Intro"}]]
   {:title "Song name" :artist "Artist"}])

(defn zipper
  "Builds the sheet zipper"
  [data]
  (zip/zipper vector? first #(assoc %1 0 %2) data))

(defn navigate-to
  "Moves the zipper to the child with given id"
  [sheet id]
  (loop [loc sheet]
    (cond
      (end? loc) nil
      (= id (:id (node loc))) loc
      :else (recur (next loc)))))
