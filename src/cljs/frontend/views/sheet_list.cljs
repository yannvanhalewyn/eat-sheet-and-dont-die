(ns frontend.views.sheet-list
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn component [{:keys [user]}]
  (let [sheets @(subscribe [:sub/sheets user])]
    [:div
     [:button.btn {:on-click #(dispatch [:playlist/create-sheet (:db/id user)])} "Create!"]
     [:h1 "My Sheets"]
     (for [{:keys [:db/id :sheet/artist :sheet/title]} sheets]
       ^{:key id}
       [:div
        [:a {:href (str "#sheets/" id)}
         [:h2 title " - " [:small artist]]]
        [:button.btn {:on-click #(dispatch [:playlist/destroy-sheet id])} "Remove"]])]))
