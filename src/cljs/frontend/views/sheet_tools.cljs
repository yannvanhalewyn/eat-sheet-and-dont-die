(ns frontend.views.sheet-tools
  (:require [frontend.util.util :refer [stop-propagation]]
            [re-frame.core :refer [dispatch]]))

(defn- handler [type]
  (stop-propagation
    (fn []
      (dispatch [:sheet/add-symbol type]))))

(defn component [props]
  [:div.sheet-tools
   [:button.sheet-tool {:on-click (handler :bar/start-repeat)}
    [:div.sheet-tool--start-repeat]]
   [:button.sheet-tool {:on-click (handler :bar/end-repeat)}
    [:div.sheet-tool--end-repeat]]
   [:button.sheet-tool {:on-click (handler :bar/segno)}
    [:div.sheet-tool--segno]]
   [:button.sheet-tool {:on-click (handler :bar/coda)}
    [:div.sheet-tool--coda]]
   [:button.sheet-tool {:on-click (handler :bar/textbox)}
    [:div.sheet-tool--text "ADD TEXT"]]])
