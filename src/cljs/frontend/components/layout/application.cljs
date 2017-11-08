(ns frontend.components.layout.application
  (:require [frontend.components.sheet :as sheet]))

(defn component [props]
  [:div.u-max-height
   [:div.navbar
    [:div.navbar__home
     [:a.navbar__item--icon.u-block {:href "/#"}
      [:i.material-icons "home"]]]

    [:div.navbar__breadcrumbs]
    [:div.navbar__search
     [:div.typeahead
      [:div.typeahead__icon
       [:i.material-icons "search"]]
      [:input.typeahead__input {:type "text" :placeholder "Zoek..."}]]]
    [:div.navbar__right
     [:div.navbar__item.navbar__item--icon
      [:i.material-icons "person"]]]]
   [:div.l-app.l-content
    [sheet/component props]]])
