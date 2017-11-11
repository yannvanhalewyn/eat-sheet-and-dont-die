(ns frontend.views.sheet-tools
  (:require [frontend.util.util :refer [stop-propagation]]
            [re-frame.core :refer [dispatch]]))

(defn- handler [type]
  (stop-propagation
    (fn []
      (dispatch [:sheet/toggle type]))))

(defn component [props]
  [:div.sheet-tools
   [:button.sheet-tool {:on-click (handler :bar/start-repeat)}
    [:div.barline--start-repeat]]
   [:button.sheet-tool {:on-click (handler :bar/end-repeat)}
    [:div.barline--end-repeat]]])
