(ns frontend.views.bar
  (:require [frontend.models.chord :refer [parse]]
            [frontend.views.chord :refer [editable-chord displayed-chord]]
            [frontend.views.util.draggable :as draggable]
            [re-frame.core :refer [dispatch]]))


(def svg-ratio (/ 54 80))
(def height 30)
(def width (* height svg-ratio))

(defn component [{:keys [bar selected] :as props}]
  [:div.bar
   (when-let [{:keys [coord/x coord/y]} (:bar/segno bar)]
     [draggable/component
      {:class (str "music-symbol music-symbol--segno")
       :style {:width width :height height}
       :on-drag-end #(.log js/console %)
       :start-pos [x y]}])
   (let [width (/ 100 (count (:bar/chords bar)))]
     (for [chord (sort-by :coll/position (:bar/chords bar))]
       ^{:key (:db/id chord)}
       [:div {:style {:display "inline-block"
                      :width (str width "%")
                      :margin-right "5px"}}
        (if (= selected (:db/id chord))
          [editable-chord {:chord chord}]
          [displayed-chord {:chord (parse (:chord/value chord))
                            :on-click #(dispatch [:sheet/select-chord (:db/id chord)])}])]))
   (when-let [{:keys [coord/x coord/y]} (:bar/coda bar)]
     [draggable/component {:style {:width height :height height}
                           :class "music-symbol music-symbol--coda"
                           :mode :align-right
                           :on-drag-end #(.log js/console %)
                           :start-pos [x y]}])])
