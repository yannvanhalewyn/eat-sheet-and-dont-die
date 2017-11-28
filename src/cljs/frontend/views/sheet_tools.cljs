(ns frontend.views.sheet-tools
  (:require [frontend.util.util :refer [stop-propagation]]
            [re-frame.core :refer [dispatch]]))

(defn- handler [type]
  (stop-propagation
    (fn []
      (dispatch [:sheet/add-symbol type]))))

(defn- tool [dispatch-key]
  (fn [{:keys [type]} children]
    [:button.sheet-tool-btn {:on-click (stop-propagation #(dispatch [dispatch-key type]))}
     [:div.sheet-tool {:class (str "sheet-tool--" (name type))}
      children]]))

(def bar-tool (tool :sheet/add-bar-attachment))
(def chord-tool (tool :sheet/add-chord-attachment))

(defn component [{:keys [selection]}]
  (when (= :selection/chord (:selection/type selection))
    [:div.sheet-tools
     (for [type [:bar/start-repeat :bar/end-repeat :attachment/segno :attachment/coda
                 :attachment/textbox :bar/repeat-cycle]]
       ^{:key type}
       [bar-tool {:type type}])
     [bar-tool {:type :bar/time-signature}
      [:div.bar__time-signature
       [:div 4]
       [:div 4]]]
     [chord-tool {:type :chord/fermata}]]))
