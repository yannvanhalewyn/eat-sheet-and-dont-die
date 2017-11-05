(ns sheet-bucket.components.layout.application
  (:require [sheet-bucket.components.sheet :as sheet]))

(defn component [props]
  [:div.u-max-height
   [:div.navbar
    [:div.navbar__home
     [:a.navbar__item.u-block {:href "/#"}
      [:i.material-icons "home"]]]

    [:div.navbar__breadcrumbs]
    [:div.navbar__right
     [:div.navbar__item
      [:i.material-icons "person"]]]]
   [sheet/component props]])
