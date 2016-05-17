(ns sheet-bucket.components.chord
  (:require [clojure.string :as str]))

(def style
  {:chord {:font-family "NashvillechordSymbols"
           :font-size "1.4em"}
   :extension {:font-size "0.7em"
               :position "relative"
               :bottom "9px" :left "3px"}})

(defn base
  [{:keys [root triad]}]
  (str (str/upper-case root)
       (when (= :minor triad) "-")))

(defn extension
  "Returns a string suitable for our chord symbols font"
  [{:keys [root triad seventh nineth]}]
  (str (if (not nineth) (case seventh :minor "7" :major "y" ""))
       (case nineth :minor "9" :major "y9" "")))

(defn component [state]
  [:span {:style (:chord style)}
   [:span (base state)]
   [:span {:style (:extension style)} (extension state)]])

