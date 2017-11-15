(ns frontend.views.util.draggable
  (:require [goog.events :as events]
            [goog.events.EventType :refer [MOUSEMOVE MOUSEUP]]
            [frontend.util.util :refer [stop-propagation] :as util]
            [reagent.core :as r]))

(defn- mouse-move-handler [position start-offset start-pos]
  (fn [e]
    (let [travel (map - (util/e->pos e) start-pos)]
      (swap! position assoc :offset (map + start-offset travel)))))

(defn- mouse-down [drag e]
  (let [start-pos (util/e->pos e)
        start-offset (:offset @drag)
        on-move (mouse-move-handler drag start-offset start-pos)]
    (events/listen js/window MOUSEMOVE on-move)
    (events/listen js/window MOUSEUP
      #(events/unlisten js/window MOUSEMOVE on-move))))

(defn component [{:keys [start-pos]}]
  (let [drag (r/atom {:offset start-pos})]
    (fn [props]
      (let [[x y] (:offset @drag)]
        [:div.draggable {:on-mouse-down (partial mouse-down drag)
                         :on-click (stop-propagation identity)
                         :class (:class props)
                         :style (assoc (:style props)
                                  :position "absolute"
                                  :top y
                                  :left (when-not (= :align-right (:mode props)) x)
                                  :right (when (= :align-right (:mode props)) (- x)))}]))))
