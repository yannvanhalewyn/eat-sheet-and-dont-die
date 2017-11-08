(ns frontend.components.section
  (:require [frontend.components.bar :as bar])
  (:require-macros [frontend.util.util :refer [fori]]))

(defn row-component [{:keys [row] :as props}]
  [:div.row {:style {:margin-bottom "10px" :white-space :nowrap}}
   (fori [i bar (:row/bars row)]
     ^{:key i}
     [bar/component (-> (dissoc props :row :attrs)
                        (assoc :bar bar))])])

(defn component [{:keys [section] :as props}]
  [:div.section
   [:h4.u-margin-top (:section/title section)]
   (fori [i row (:section/rows section)]
     ^{:key i}
     [row-component (-> (dissoc props :section) (assoc :row row))])])
