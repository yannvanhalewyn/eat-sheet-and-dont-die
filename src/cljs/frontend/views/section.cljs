(ns frontend.views.section
  (:require [frontend.views.bar :as bar]
            [frontend.util.util :refer [presence]]
            [frontend.views.editable :as editable]
            [re-frame.core :refer [dispatch]])
  (:require-macros [shared.utils :refer [fori]]))

(defn row-component [{:keys [row] :as props}]
  [:div.row {:style {:margin-bottom "10px" :white-space :nowrap}}
   (fori [i bar (:row/bars row)]
     ^{:key i}
     [bar/component (-> (dissoc props :row :attrs)
                        (assoc :bar bar))])])

(defn component [{:keys [section] :as props}]
  [:div.section
   (let [title (or (presence (:section/title section)) "(section)")]
     [editable/component {:on-change #(dispatch [:sheet/set-section-title section %])
                          :value title}
      [:h4.u-margin-top title]])
   (fori [i row (:section/rows section)]
     ^{:key i}
     [row-component (-> (dissoc props :section) (assoc :row row))])])
