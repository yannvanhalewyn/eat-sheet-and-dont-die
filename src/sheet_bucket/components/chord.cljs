(ns sheet-bucket.components.chord
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(def style
  {:chord {:font-family "NashvillechordSymbols"
           :cursor "pointer"
           :font-size "1.4em"}
   :extension {:font-size "0.7em"
               :position "relative"
               :bottom "9px" :left "3px"}})

(defn- base
  "Returns a string suitable for the chord symbols font for the base
  root and triad"
  [{:keys [root triad]}]
  (str (str/upper-case root)
       (when (= :minor triad) "-")))

(defn- extension
  "Returns a string suitable for our chord symbols font for the
  extension"
  [{:keys [root triad seventh nineth]}]
  (str (if (not nineth) (case seventh :minor "7" :major "y" ""))
       (case nineth :minor "9" :major "y9" "")))

(defn displayed-chord
  "A displayable formatted chord"
  [props]
  [:span {:style (:chord style) :on-click (:on-click props)}
   [:span (base props)]
   [:span {:style (:extension style)} (extension props)]])

(defn editable-chord
  "An input box for editing a chord"
  []
  (reagent/create-class
   {:component-did-mount
    (fn [this] (.focus (reagent/dom-node this)))
    :reagent-render
    (fn [{:keys [on-blur] :as props}]
      [:input {:type "text"
               :on-blur #(on-blur (.. % -target -value))
               :default-value (:text props)}])}))
