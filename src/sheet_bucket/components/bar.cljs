(ns sheet-bucket.components.bar
  (:require [sheet-bucket.models.chord :refer [parse]]
            [sheet-bucket.components.chord :refer [editable-chord displayed-chord]]))

(def style {:width "200px" :margin-right "50px" :display "inline-block"})

(defn component [{:keys [chords selected on-chord-click] :as props}]
  [:div
   {:style (merge style (when (> (count chords) 1) {:border-bottom "2px solid black"}))}
   (let [width (/ 100 (count chords))]
     (for [{:keys [id] :as chord} chords]
       ^{:key id}
       [:div {:style {:display "inline-block" :width (str width "%")}}
        (if (= selected id)
          [editable-chord (-> (dissoc props :chords :selected :on-chord-click)
                              (assoc :text (:raw chord)))]
          [displayed-chord (assoc (parse (:raw chord)) :on-click (partial on-chord-click id))])]))])
