(ns sheet-bucket.components.chord
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(def style
  {:chord {:font-family "NashvillechordSymbols"
           :cursor "pointer"
           :font-size "1.7em"}
   :extension {:font-size "0.7em"
               :position "relative"
               :bottom "9px" :left "3px"}})

(defn- base
  "Returns a string suitable for the chord symbols font for the base
  root and triad"
  [{[root accidental] :root triad :triad}]
  (str (str/upper-case root)
       (condp = accidental :flat "@" :sharp "#" "")
       (when (= :minor triad) "-")))

(defn- extension
  "Returns a string suitable for our chord symbols font for the
  extension"
  [{:keys [root triad seventh ninth]}]
  (str (when (= seventh :major) "y")
       (when (and seventh (not ninth)) "7")
       (case ninth :natural "9" "")))

(defn displayed-chord
  "A displayable formatted chord"
  [{[root accidental] :root :as props}]
  [:span {:style (:chord style) :on-click (:on-click props)}
   (if (:root props)
     [:span
      [:span (base props)]
      [:span {:style (:extension style)} (extension props)]])])

(defn editable-chord
  "An input box for editing a chord"
  []
  (reagent/create-class
   {:component-did-mount
    (fn [this] (.focus (reagent/dom-node this)))
    :reagent-render
    (fn [{:keys [on-blur] :as props}]
      [:input {:type "text"
               :style {:width "100%"}
               :on-blur #(on-blur (.. % -target -value))
               :default-value (:text props)}])}))
