(ns frontend.views.section
  (:require [frontend.views.bar :as bar]
            [frontend.views.editable :as editable]
            [shared.utils :refer [mappad presence]]
            [re-frame.core :refer [dispatch]]))

(defn- barline
  "Given a bar and it's next bar, return the suitable bar seperator"
  [prev next]
  (with-meta
    (case [(boolean (:bar/end-repeat prev)) (boolean (:bar/start-repeat next))]
      [true true] [:i.barline--both-repeat]
      [true false] [:i.barline--end-repeat]
      [false true] [:i.barline--start-repeat]
      (when next [:i.barline]))
    {:key (str "barline" (:db/id prev) "-" (:db/id next))}))

(defn- slurs [{:keys [bars]}]
  [:div.l-flex-row
   (for [bar bars]
     ^{:key (str "SLURS-" (:db/id bar))}
     [:div.flex-bar
      (when-let [cycle (:bar/repeat-cycle bar)]
        [:div.repeat-cycle
         [editable/component
          {:on-change #(dispatch [:sheet/set-repeat-cycle (:db/id bar) %])
           :value cycle}
          cycle]])])])

(defn spacer []
  [:div.spacer {:style {:height 25}}])

(defn row-component [{:keys [row selection]}]
  (let [bars (:row/bars row)]
    [:div
     (when (and (some (complement empty?) (map :bar/attachments bars))
             (not (some seq (map :bar/repeat-cycle bars)))) [spacer])
     [slurs {:bars (:row/bars row)}]
     [:div.row
      (when (:bar/start-repeat (first bars)) [:i.barline--start-repeat])
      (interleave
        (for [bar (sort-by :coll/position bars)]
          ^{:key (:db/id bar)}
          [bar/component {:bar bar :selection selection}])
        (mappad nil barline bars (drop 1 bars)))]]))

(defn component [{:keys [section selection]}]
  [:div.section
   (let [title (or (presence (:section/title section)) "(section)")]
     [editable/component {:on-change #(dispatch [:sheet/set-section-title section %])
                          :value title}
      [:h4.u-margin-top title]])
   (for [row (sort-by :coll/position (:section/rows section))]
     ^{:key (:db/id row)}
     [row-component {:row row :selection selection}])])
