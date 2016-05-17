(ns sheet-bucket.components.chord
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(def style
  {:chord {:font-family "NashvillechordSymbols"
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

(defn- chord-show
  "A displayable formatted chord"
  [props]
  [:span {:style (:chord style)}
   [:span (base props)]
   [:span {:style (:extension style)} (extension props)]])

(defn- chord-edit
  "An input box for editing a chord"
  []
  (reagent/create-class
   {:component-did-mount
    (fn [this] (.focus (.findDOMNode js/ReactDOM this)))
    :reagent-render
    (fn [props]
      [:input {:type "text"
               :default-value (:chord-text props)}])}))

(defn component [props]
  (if (:focused props)
    [chord-edit props]
    [chord-show props]))

