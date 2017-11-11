(ns frontend.views.section
  (:require [frontend.views.bar :as bar]
            [frontend.util.util :refer [presence]]
            [frontend.views.editable :as editable]
            [shared.utils :refer [mappad]]
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

(defn row-component [{:keys [row] :as props}]
  (let [bars (:row/bars row)]
    [:div.row {:style {:margin-bottom "10px" :white-space :nowrap}}
     (when (:bar/start-repeat (first bars)) [:i.barline--start-repeat])
     (interleave
       (for [bar (sort-by :coll/position bars)]
         ^{:key (:db/id bar)}
         [bar/component (-> (dissoc props :row :attrs)
                          (assoc :bar bar))])
       (mappad nil barline bars (drop 1 bars)))]))

(defn component [{:keys [section] :as props}]
  [:div.section
   (let [title (or (presence (:section/title section)) "(section)")]
     [editable/component {:on-change #(dispatch [:sheet/set-section-title section %])
                          :value title}
      [:h4.u-margin-top title]])
   (for [row (sort-by :coll/position (:section/rows section))]
     ^{:key (:db/id row)}
     [row-component (-> (dissoc props :section) (assoc :row row))])])
