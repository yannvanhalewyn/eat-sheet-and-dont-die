(ns frontend.views.bar
  (:require [frontend.models.chord :refer [parse]]
            [frontend.views.chord :refer [editable-chord displayed-chord]]))

(defn start-repeat [] [:img.music-symbol {:src "/img/start-repeat.svg"}])
(defn end-repeat [] [:img.music-symbol {:src "/img/end-repeat.svg"}])
(defn both-repeat [] [:img.music-symbol {:src "/img/both-repeat.svg"}])

(defn component [{:keys [bar selected on-chord-click] :as props}]
  [:div.bar
   (when (:bar/start-repeat bar) [start-repeat])
   (let [width (/ 100 (count (:bar/chords bar)))]
     (for [chord (:bar/chords bar)]
       ^{:key (:db/id chord)}
       [:div {:style {:display "inline-block"
                      :width (str width "%")
                      :margin-right "5px"}}
        (if (= selected (:db/id chord))
          [editable-chord (assoc (select-keys props [:append :remove :move :deselect :update-chord])
                            :chord chord)]
          [displayed-chord {:chord (parse (:chord/value chord))
                            :on-click (partial on-chord-click (:db/id chord)) }])]))
   (when (:bar/end-repeat bar) [end-repeat])])
