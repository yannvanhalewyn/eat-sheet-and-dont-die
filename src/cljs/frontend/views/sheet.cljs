(ns frontend.views.sheet
  (:require [frontend.views.section :as section]
            [re-frame.core :refer [subscribe dispatch]])
  (:require-macros [shared.utils :refer [fori]]))

(defn component [{:keys [sheet deselect append] :as props}]
  (let [sheet @(subscribe [:sub/sheet])
        selected @(subscribe [:sub/selected])]
    [:div.u-max-height {:on-click #(dispatch [:sheet/deselect])}
     [:h1 (:sheet/title sheet)]
     [:h3.u-margin-top--s (:sheet/artist sheet)]
     [:div.u-margin-top.sections
      (fori [i section (:sheet/sections sheet)]
        ^{:key i} [section/component
                   {:section section
                    :selected selected
                    :on-chord-click #(dispatch [:sheet/select-chord %])
                    :update-chord #(dispatch [:sheet/update-chord %])
                    :append #(dispatch [:sheet/append %])
                    :move #(dispatch [:sheet/move %])
                    :remove #(dispatch [:sheet/remove %])}])]]))
