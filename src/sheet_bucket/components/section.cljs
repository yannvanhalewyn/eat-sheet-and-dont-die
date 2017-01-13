(ns sheet-bucket.components.section
  (:require [sheet-bucket.components.bar :as bar])
  (:require-macros [sheet-bucket.util.util :refer [fori]]))

(defn row-component [{:keys [bars]}]
  [:div.row {:style {:margin-bottom "10px" :white-space :nowrap}}
   (doall
    (map-indexed
     (fn [i bar] ^{:key i} [bar/component {:chords bar}])
     bars))])

(defn component [{:keys [name rows]}]
  [:div.section
   [:h3 name]
   (doall
    (map-indexed
     (fn [i row]
       ^{:key (gensym)} [row-component {:bars row}])
     rows))])
