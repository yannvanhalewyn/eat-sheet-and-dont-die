(ns frontend.views.bar
  (:require [frontend.models.chord :refer [parse]]
            [frontend.views.chord :refer [editable-chord displayed-chord]]
            [re-frame.core :refer [dispatch]]))

(defn component [{:keys [bar selected] :as props}]
  [:div.bar
   (when (:bar/segno bar)
     [:div.music-symbol {:class (str "music-symbol--segno"
                                  (if (zero? (:coll/position bar)) "-first-bar"))}])
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
   (when-let [coda (:bar/coda bar)]
     (if (= :start coda)
       (if (zero? (:coll/position bar))
         [:div.music-symbol.music-symbol--coda-start-first-bar]
         [:div.music-symbol.music-symbol--coda-start])
       [:div.music-symbol.music-symbol--coda-end]))])
