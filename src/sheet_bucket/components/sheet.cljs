(ns sheet-bucket.components.sheet
  (:require [sheet-bucket.components.section :as section])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn component [{:keys [title artist sections] :as props}]
  [:div.sheet
   [:h1 title]
   [:h2 artist]
   (fori [i section sections]
     ^{:key i} [section/component
                (-> props (dissoc :title :artist :sections) (merge section))])])
