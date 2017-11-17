(ns frontend.models.sheet-symbol
  (:require [frontend.models.sheet :as sheet]
            [clojure.zip :as zip :refer [up]]
            [shared.utils :as sutil]))

;; Constants
;; =========
(def height 30)
(def segno-svg-ratio (/ 54 80))
(def segno-width (* height segno-svg-ratio))
(def coda-width height)

(def floor (.-floor js/Math))

(defn make [type]
  (case type
    :bar/coda {:db/id (sutil/gen-temp-id)
               :symbol/type :symbol/coda
               :coord/x (floor (/ coda-width 2))
               :coord/y (floor (- -4 height))}
    :bar/segno {:db/id (sutil/gen-temp-id)
                :symbol/type :symbol/segno
                :coord/x (floor (/ (- segno-width) 2))
                :coord/y (floor (- -4 height))}
    :bar/textbox {:db/id (sutil/gen-temp-id)
                  :symbol/type :symbol/textbox
                  :text/value "My Text"
                  :coord/x 0
                  :coord/y 30}
    nil))

(defn add [loc type]
  (case type
    (:bar/start-repeat :bar/end-repeat) (zip/edit (up loc) update type not)
    (zip/edit (up loc) update :bar/symbols #(conj % (make type)))))

(defn move [sheet bar-id symbol-id [x y]]
  (-> (sheet/navigate-to (sheet/zipper sheet) bar-id)
    (zip/edit update :bar/symbols
      (fn [symbols] (map
                      #(if (= (:db/id %) symbol-id)
                         (assoc % :coord/x (floor x) :coord/y (floor y))
                         %)
                      symbols)))
    zip/root))

(defn edit [sheet bar-id symbol-id value]
  (-> (sheet/navigate-to (sheet/zipper sheet) bar-id)
    (zip/edit update :bar/symbols
      (fn [symbols] (map
                      #(if (= (:db/id %) symbol-id)
                         (assoc % :text/value value)
                         %)
                      symbols)))
    zip/root))
