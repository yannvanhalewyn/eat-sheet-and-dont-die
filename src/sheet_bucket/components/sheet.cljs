(ns sheet-bucket.components.sheet
  (:require [sheet-bucket.components.section :as section])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn component [{{:keys [artist title]} :attrs
                  :keys [deselect sections append]
                  :as props}]
  [:div {:on-click deselect}
   [:h1 title]
   [:h3.u-margin-top--s artist]
   [:div.u-margin-top.sections
    (fori [i [rows attrs] sections]
      ^{:key i} [section/component
                 (-> props
                     (dissoc :attrs :sections)
                     (assoc :rows rows :attrs attrs))])]])
