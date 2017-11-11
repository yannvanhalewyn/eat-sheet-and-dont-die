(ns frontend.views.sheet
  (:require [frontend.views.section :as section]
            [frontend.views.editable :as editable]
            [frontend.views.sheet-tools :as sheet-tools]
            [frontend.util.util :refer [presence]]
            [re-frame.core :refer [subscribe dispatch]])
  (:require-macros [shared.utils :refer [fori]]))

(defn component [{:keys [sheet-id sheet deselect append] :as props}]
  (let [sheet @(subscribe [:sub/sheet sheet-id])
        selected @(subscribe [:sub/selected])]
    [:div.u-max-height {:on-click #(dispatch [:sheet/deselect])}
     (let [title (or (presence (:sheet/title sheet)) "(title)")
           artist (or (presence (:sheet/artist sheet)) "(artist)")]
       [:div
        [editable/component {:on-change #(dispatch [:sheet/set-title %])
                             :value title}
         [:h1 title]]
        [editable/component {:on-change #(dispatch [:sheet/set-artist %])
                             :value artist}
         [:h3.u-margin-top--s artist]]])
     [:div.u-margin-top.sections
      (fori [i section (:sheet/sections sheet)]
        ^{:key i} [section/component
                   {:section section
                    :selected selected
                    :on-chord-click #(dispatch [:sheet/select-chord %])
                    :update-chord #(dispatch [:sheet/update-chord %])
                    :append #(dispatch [:sheet/append %])
                    :move #(dispatch [:sheet/move %])
                    :remove #(dispatch [:sheet/remove %])}])]
     [sheet-tools/component]]))
