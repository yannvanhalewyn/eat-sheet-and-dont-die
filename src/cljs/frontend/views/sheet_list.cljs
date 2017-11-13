(ns frontend.views.sheet-list
  (:require [re-frame.core :refer [subscribe dispatch]]
            [frontend.util.util :refer [stop-propagation confirm]]
            [goog.string :refer [format]]))

(defn component [{:keys [user]}]
  (let [sheets @(subscribe [:sub/sheets user])]
    [:div
     [:button.btn.u-pull-right {:on-click #(dispatch [:playlist/create-sheet (:db/id user)])}
      "+ New sheet"]
     [:h1 "My Sheets"]
     [:div.u-margin-top--l
      (for [{:keys [:db/id :sheet/artist :sheet/title]} sheets]
        ^{:key id}
        [:a.l-flex-row.l-space-between.sheet-list-item
         {:href (str "#sheets/" id)}
         [:div
          [:div [:strong title]]
          [:small artist]]
         [:button.icon-btn.icon-btn--red {:on-click
                                          (confirm (format "%s van %s verwijderen?" title artist)
                                            dispatch [:playlist/destroy-sheet id])}
          [:i.material-icons "delete"]]])]]))
