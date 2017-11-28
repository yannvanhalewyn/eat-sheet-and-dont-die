(ns frontend.views.layout.application
  (:require [frontend.views.sheet :as sheet]
            [frontend.views.sheet-list :as sheet-list]
            [re-frame.core :refer [subscribe]]))

(defn active-panel []
  (if-let [current-user @(subscribe [:sub/current-user])]
    (let [route @(subscribe [:sub/active-route])]
      (case (:route/handler route)
        (:route/index :route/sheets) [sheet-list/component {:user current-user}]
        :route/sheet [sheet/component {:sheet-id (js/parseInt (get-in route [:route/params :sheet/id]))}]))
    [:h1 "Login panel"]))

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
   [:div.l-app
    [active-panel]]])
