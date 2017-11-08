(ns sheet-bucket.components.sheet
  (:require [sheet-bucket.components.section :as section])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn component [{:keys [sheet deselect append] :as props}]
  [:div.u-max-height {:on-click deselect}
   [:h1 (:sheet/title sheet)]
   [:h3.u-margin-top--s (:sheet/artist sheet)]
   [:div.u-margin-top.sections
    (fori [i section (:sheet/sections sheet)]
      ^{:key i} [section/component
                 (-> (dissoc props :sheet)
                     (assoc :section section))])]])
