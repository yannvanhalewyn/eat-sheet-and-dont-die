(ns frontend.models.sheet-symbol
  (:require [clojure.zip :as zip :refer [up]]))

(defn- position [x y] {:coord/x x :coord/y y})

;; Constants
;; =========
(def height 30)
(def segno-svg-ratio (/ 54 80))
(def segno-width (* height segno-svg-ratio))
(def coda-width height)

(def defaults
  {:bar/coda (position (/ coda-width 2) (- -4 height))
   :bar/segno (position (/ (- segno-width) 2) (- -4 height))
   :bar/start-repeat true
   :bar/end-repeat true})

(defn add [loc type]
  (let [value (or (defaults type) (position 0 0))]
    (zip/edit (up loc) update type #(if % nil value))))

(defn move [loc type pos]
  (zip/edit (up loc) assoc type (apply position pos)))
