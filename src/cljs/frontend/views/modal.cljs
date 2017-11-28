(ns frontend.views.modal
  (:require [reagent.core :as r]))

(defn component []
  (r/create-class
    {:component-will-mount
     #(set! (-> js/document .-body .-style .-overflow) "hidden")
     :component-will-unmount
     #(set! (-> js/document .-body .-style .-overflow) "initial")
     :reagent-render
     (fn [{:keys [on-close]} children]
       [:div.modal
        [:div.modal__background {:on-click on-close}]
        [:div.modal__content
         [:button.icon-btn.icon-btn--grey.modal__close-btn {:on-click on-close}
          [:i.material-icons "close"]]
         children]])}))
