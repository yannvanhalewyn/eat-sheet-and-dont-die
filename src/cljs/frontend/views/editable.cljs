(ns frontend.views.editable
  (:require [reagent.core :as r]
            [goog.events.KeyCodes :refer [ENTER ESC TAB]]))

(defn component []
  (let [editing (r/atom false)]
    (r/create-class
      {:component-did-update
       (fn [this]
         (when @editing
           (.focus (r/dom-node this))
           (.select (r/dom-node this))))
       :reagent-render
       (fn [{:keys [on-change value]} children]
         (if @editing
           [:input {:type "text"
                    :default-value value
                    :on-key-down (fn [e]
                                   (when (#{ENTER ESC TAB} (.-which e))
                                     (reset! editing false)
                                     (on-change (.. e -target -value))))
                    :on-blur (fn [e]
                               (reset! editing false)
                               (on-change (.. e -target -value)))}]
           [:span {:on-click #(reset! editing true)}
            children]))})))
