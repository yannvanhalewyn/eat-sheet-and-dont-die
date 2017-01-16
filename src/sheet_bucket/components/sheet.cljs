(ns sheet-bucket.components.sheet
  (:require [sheet-bucket.components.section :as section])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn component [{{:keys [artist title]} :attrs :keys [sections] :as props}]
  [:div.sheet
   [:h1 title]
   [:h2 artist]
   (fori [i [rows attrs] sections]
     ^{:key i} [section/component
                (-> props
                    (dissoc :attrs :sections)
                    (assoc :rows rows :attrs attrs))])])
