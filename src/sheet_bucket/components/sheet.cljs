(ns sheet-bucket.components.sheet
  (:require [sheet-bucket.components.section :as section]
            [sheet-bucket.util.util :refer [stop-propagation]])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn component [{{:keys [artist title]} :attrs
                  :keys [clear-selected sections add-element]
                  :as props}]
  [:div.sheet {:on-click clear-selected}
   [:h1 title]
   [:h2 artist]
   (for [type [:chord :bar :row :section]]
     ^{:key type}
     [:button {:on-click (stop-propagation add-element type)} (str "Add " (name type))])
   (fori [i [rows attrs] sections]
     ^{:key i} [section/component
                (-> props
                    (dissoc :attrs :sections)
                    (assoc :rows rows :attrs attrs))])])
