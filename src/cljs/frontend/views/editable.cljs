(ns frontend.views.editable
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.events.EventType :refer [KEYDOWN]]
            [goog.events.KeyCodes :refer [ENTER ESC TAB]]))

(defn component []
  (let [editing (r/atom false)]
    (r/create-class
      {:component-did-update
       (fn [this]
         (when @editing
           (doto (r/dom-node this) .focus .select
                 (events/listen KEYDOWN
                   (fn [e]
                     ;; Don't intervene with editor's keybindings
                     (.stopPropagation e)
                     (when (#{ENTER ESC TAB} (.-keyCode e))
                       (reset! editing false)
                       ((:on-change (r/props this)) (.. e -target -value))))))))
       :reagent-render
       (fn [{:keys [on-change value edit-trigger]} children]
         (if @editing
           [:input.editable
            {:type "text"
             :default-value value
             :on-blur (fn [e]
                        (reset! editing false)
                        (on-change (.. e -target -value)))}]
           [:span {(or edit-trigger :on-click) #(reset! editing true)}
            children]))})))
