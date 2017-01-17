(ns sheet-bucket.components.sheet
  (:require [sheet-bucket.components.section :as section]
            [sheet-bucket.util.util :refer [stop-propagation]])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn component [{{:keys [artist title]} :attrs
                  :keys [clear-selected sections add-bar]
                  :as props}]
  [:div.sheet {:on-click clear-selected}
   [:h1 title]
   [:h2 artist]
   [:button {:on-click (stop-propagation add-bar)} "Add bar"]
   (fori [i [rows attrs] sections]
     ^{:key i} [section/component
                (-> props
                    (dissoc :attrs :sections)
                    (assoc :rows rows :attrs attrs))])])
