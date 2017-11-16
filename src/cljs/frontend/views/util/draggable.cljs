(ns frontend.views.util.draggable
  (:require [goog.events :as events]
            [goog.events.EventType :refer [MOUSEMOVE MOUSEUP]]
            [frontend.util.util :refer [stop-propagation] :as util]
            [reagent.core :as r]))

(defn- mouse-move-handler [position start-offset start-pos]
  (fn [e]
    (let [travel (map - (util/e->pos e) start-pos)]
      (swap! position assoc :offset (map + start-offset travel)))))

(defn- mouse-down-handler [drag props]
  (fn [e]
    (let [start-pos (util/e->pos e)
          start-offset (:offset @drag)
          on-move (mouse-move-handler drag start-offset start-pos)]
      (.preventDefault e)
      (events/listen js/window MOUSEMOVE on-move)
      (events/listen js/window MOUSEUP
        #(events/unlisten js/window MOUSEMOVE on-move)))))

(defn component [{:keys [start-pos]}]
  (let [drag (r/atom {:offset start-pos})]
    (fn [{:keys [on-click on-drag-end mode] :as props}]
      (let [[x y] (:offset @drag)]
        [:div.draggable {:on-mouse-down (mouse-down-handler drag props)
                         :on-mouse-up (when-let [callback on-drag-end]
                                        #(callback (:offset @drag)))
                         :on-click (stop-propagation on-click)
                         :class (:class props)
                         :style (assoc (:style props)
                                  :position "absolute"
                                  :top y
                                  :left (when-not (= :align-right mode) x)
                                  :right (when (= :align-right mode) (- x)))}]))))
