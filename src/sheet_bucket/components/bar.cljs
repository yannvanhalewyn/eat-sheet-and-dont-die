(ns sheet-bucket.components.bar
  (:require [sheet-bucket.models.chord :refer [parse]]
            [sheet-bucket.components.chord :refer [editable-chord displayed-chord]]))

(defn component [{:keys [bar selected on-chord-click] :as props}]
  [:div.bar
   (let [width (/ 100 (count (:bar/chords bar)))]
     (for [chord (:bar/chords bar)]
       ^{:key (:chord/id chord)}
       [:div {:style {:display "inline-block" :width (str width "%")}}
        (if (= selected (:chord/id chord))
          [editable-chord (-> (dissoc props :bar :selected :on-chord-click)
                              (assoc :chord chord))]
          [displayed-chord {:chord (parse (:chord/value chord))
                            :on-click (partial on-chord-click (:chord/id chord)) }])]))])
