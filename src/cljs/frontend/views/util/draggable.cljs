(ns frontend.views.util.draggable
  (:require [goog.events :as events]
            [goog.events.EventType :refer [MOUSEMOVE MOUSEUP]]
            [frontend.util.util :refer [stop-propagation] :as util]
            [reagent.core :as r]))

(defn- mouse-move-handler [drag el-start-pos drag-start-pos]
  (fn [e]
    (let [travel (map - (util/e->pos e) drag-start-pos)]
      (reset! drag (map + el-start-pos travel)))))

(defn- mouse-down-handler [drag props]
  (fn [e]
    (let [drag-start-pos (util/e->pos e)
          on-move (mouse-move-handler drag (:pos props) drag-start-pos)]
      (.preventDefault e)
      (events/listen js/window MOUSEMOVE on-move)
      (events/listen js/window MOUSEUP
        #(events/unlisten js/window MOUSEMOVE on-move)))))

(defn component [{:keys [pos]}]
  (let [drag (r/atom pos)]
    (r/create-class
      {:component-will-receive-props
       ;; When new data comes in not following a drag, like via websockets
       (fn [_ [_ props]]
         (reset! drag (:pos props)))
       :reagent-render
       (fn [{:keys [on-click on-drag-end mode] :as props} & children]
         (let [[x y] @drag]
           (into
             [:div.draggable {:on-mouse-down (mouse-down-handler drag props)
                              :on-mouse-up (when-let [callback on-drag-end]
                                             #(callback @drag))
                              :on-click (stop-propagation (or on-click identity))
                              :class (:class props)
                              :style (assoc (:style props)
                                       :position "absolute"
                                       :top y
                                       :left (when-not (= :align-right mode) x)
                                       :right (when (= :align-right mode) (- x)))}]
             children)))})))
