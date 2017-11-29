(ns frontend.views.modal
  (:require [frontend.views.util.select :as select]
            [frontend.util.util :refer [stop-propagation]]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]))

(defn modal-wrapper []
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

(defn ts-modal [{:keys [bar]}]
  (let [bar (or bar @(subscribe [:sub/current-bar]))
        time-signature (r/atom (or (:bar/time-signature bar)
                                 {:time-signature/beat 4
                                  :time-signature/beat-type 4}))]
    (fn []
      [:div.l-content
       [:h3.t-light "Time signature"]
       [:p.u-margin-top "Change the time signature starting on the current bar."]
       [:div
        [select/component
         {:on-select #(swap! time-signature assoc :time-signature/beat (js/parseInt %))
          :selected (:time-signature/beat @time-signature)
          :class "time-signature-select"
          :options (range 1 33)}]]
       [:div
        [select/component
         {:on-select #(swap! time-signature assoc :time-signature/beat-type (js/parseInt %))
          :selected (:time-signature/beat-type @time-signature)
          :class "time-signature-select"
          :options [1 2 4 8 16 32]}]]
       [:div.u-margin-top--s
        (when-let [{:keys [db/id]} (:bar/time-signature bar)]
          [:button.btn.btn--red.u-margin-right
           {:on-click (stop-propagation #(dispatch [:sheet/remove-time-signature id]))}
           "Remove"])
        [:button.btn.u-margin-top--s
         {:on-click (stop-propagation
                      #(dispatch [:sheet/create-or-update-time-signature (:db/id bar)
                                  @time-signature]))}
         "Save"]]])))

(def modals
  {:modal/time-signature ts-modal})

(defn component []
  (let [{:keys [modal/key modal/props]} @(subscribe [:sub/modal])]
    (when-let [modal-body (get modals key)]
      [modal-wrapper {:on-close #(dispatch [:modal/close])}
       [modal-body props]])))
