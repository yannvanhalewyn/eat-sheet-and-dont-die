(ns frontend.views.sheet-list
  (:require [re-frame.core :refer [subscribe]]))

(defn component [{:keys [user]}]
  (let [sheets @(subscribe [:sub/sheets user])]
    [:div
     [:h1 "My Sheets"]
     (for [{:keys [:db/id :sheet/artist :sheet/title]} sheets]
       ^{:key id}
       [:a {:href (str "#sheets/" id)}
        [:h2 title " - " [:small artist]]])]))
