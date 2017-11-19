(ns frontend.views.bar
  (:require [frontend.models.chord :refer [parse]]
            [frontend.views.chord :refer [editable-chord displayed-chord]]
            [frontend.views.util.draggable :as draggable]
            [re-frame.core :refer [dispatch]]
            [frontend.views.editable :as editable]
            [shared.utils :refer [presence]]))

(def svg-ratio (/ 54 80))
(def height 30)
(def width (* height svg-ratio))

(defn attachment [{:keys [attachment bar selected]}]
  (let [{:keys [db/id coord/x coord/y textbox/value]} attachment
        props {:on-drag-end #(dispatch [:sheet/move-symbol (:db/id bar) id %])
               :on-click #(dispatch [:sheet/select :selection/attachment id])
               :start-pos [x y]}
        class (when selected "selected")]
    (case (:attachment/type attachment)
      :symbol/segno
      [draggable/component
       (assoc props
         :style {:width width :height height}
         :class (str class " music-symbol music-symbol--segno"))]

      :symbol/coda
      [draggable/component
       (assoc props
         :style {:width height :height height}
         :class (str class " music-symbol music-symbol--coda")
         :mode :align-right)]

      :attachment/textbox
      [draggable/component
       (assoc props :class (str class " draggable--textbox"))
       [editable/component {:edit-trigger :on-double-click
                            :on-change #(dispatch [:sheet/edit-textbox (:db/id bar) id %])
                            :value value}
        [:span (or (presence value) "Enter text")]]])))

(defn component [{:keys [bar selection] :as props}]
  [:div.flex-bar.bar
   ;; Attachments
   ;; =======
   (for [{:keys [db/id] :as att} (:bar/attachments bar)]
     ^{:key id}
     [attachment {:attachment att :bar bar :selected (= id (:selection/id selection))}])

   ;; Chords
   ;; ======
   (let [width (/ 100 (count (:bar/chords bar)))]
     (for [chord (sort-by :coll/position (:bar/chords bar))]
       ^{:key (:db/id chord)}
       [:div {:style {:display "inline-block"
                      :width (str width "%")
                      :margin-right "5px"}}
        (if (= (:selection/id selection) (:db/id chord))
          [editable-chord {:chord chord}]
          [displayed-chord {:chord (parse (:chord/value chord))
                            :on-click #(dispatch [:sheet/select :selection/chord (:db/id chord)])}])]))])
