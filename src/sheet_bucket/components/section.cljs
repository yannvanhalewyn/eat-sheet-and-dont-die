(ns sheet-bucket.components.section
  (:require [sheet-bucket.components.bar :as bar])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn row-component [{:keys [bars] :as props}]
  [:div.row {:style {:margin-bottom "10px" :white-space :nowrap}}
   (fori [i bar bars]
     ^{:key i} [bar/component (-> props (dissoc :bars) (assoc :chords bar))])])

(defn component [{:keys [name rows] :as props}]
  [:div.section
   [:h3 name]
   (fori [i row rows]
     ^{:key i} [row-component (-> props (dissoc :name :rows) (assoc :bars row))])])
