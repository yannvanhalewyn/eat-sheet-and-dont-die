(ns frontend.views.sheet-tools
  (:require [frontend.util.util :refer [stop-propagation]]
            [re-frame.core :refer [dispatch]]))

(defn- tool [{:keys [dispatch-evt type]} children]
  [:button.sheet-tool-btn {:on-click (stop-propagation #(dispatch dispatch-evt))}
   [:div.sheet-tool {:class (str "sheet-tool--" (name type))}
    children]])

(defn component [{:keys [selection]}]
  (when (= :selection/chord (:selection/type selection))
    [:div.sheet-tools
     (for [type [:bar/start-repeat :bar/end-repeat :attachment/segno :attachment/coda
                 :attachment/textbox :bar/repeat-cycle]]
       ^{:key type}
       [tool {:dispatch-evt [:sheet/add-bar-attachment type] :type type}])
     [tool {:dispatch-evt [:modal/show :modal/time-signature]
            :type :bar/time-signature}
      [:div.bar__time-signature [:div 4] [:div 4]]]
     [tool {:dispatch-evt [:sheet/add-chord-attachment :chord/fermata]
            :type :chord/fermata}]]))
