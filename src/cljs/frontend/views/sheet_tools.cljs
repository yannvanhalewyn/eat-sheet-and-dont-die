(ns frontend.views.sheet-tools
  (:require [frontend.util.util :refer [stop-propagation]]
            [re-frame.core :refer [dispatch]]))

(defn- handler [type]
  (stop-propagation
    (fn []
      (dispatch [:sheet/add-symbol type]))))

(defn- tool [{:keys [type]}]
  [:button.sheet-tool-btn {:on-click (stop-propagation #(dispatch [:sheet/add-symbol type]))}
   [:div.sheet-tool {:class (str "sheet-tool--" (name type))}]])

(defn component [{:keys [selection]}]
  (when (= :selection/chord (:selection/type selection))
    [:div.sheet-tools
     (for [type [:bar/start-repeat :bar/end-repeat :attachment/segno :attachment/coda
                 :attachment/textbox]]
       ^{:key type}
       [tool {:type type}])]))
