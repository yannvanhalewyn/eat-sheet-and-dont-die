(ns sheet-bucket.components.section
  (:require [sheet-bucket.components.bar :as bar])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn row-component [{:keys [bars] :as props}]
  [:div.row {:style {:margin-bottom "10px" :white-space :nowrap}}
   (fori [i [chords] bars]
     ^{:key i} [bar/component (-> props
                                  (dissoc :bars :attrs)
                                  (assoc :chords chords))])])

(defn component [{{:keys [name]} :attrs :keys [rows] :as props}]
  [:div.section
   [:h4.u-margin-top name]
   (fori [i [bars] rows]
     ^{:key i} [row-component (-> props
                                  (dissoc :attrs :rows)
                                  (assoc :bars bars))])])
