(ns frontend.models.bar-attachment
  (:require [frontend.models.sheet :as sheet]
            [clojure.zip :as zip :refer [up]]
            [shared.utils :as sutil]))

;; Constants
;; =========

(def height 30)
(def segno-svg-ratio (/ 54 80))
(def segno-width (* height segno-svg-ratio))
(def coda-width height)

;; Helpers
;; =======

(def floor (.-floor js/Math))

(defn- update-attachment [f bar-loc att-id]
  (zip/edit bar-loc update :bar/attachments
    (fn [attachments]
      (map #(if (= (:db/id %) att-id) (f %) %)
        attachments))))

(defn- make [type]
  (case type
    :attachment/coda {:db/id (sutil/gen-temp-id)
                      :attachment/type :symbol/coda
                      :coord/x (floor (/ coda-width 2))
                      :coord/y (floor (- -4 height))}
    :attachment/segno {:db/id (sutil/gen-temp-id)
                       :attachment/type :symbol/segno
                       :coord/x (floor (/ (- segno-width) 2))
                       :coord/y (floor (- -4 height))}
    :attachment/textbox {:db/id (sutil/gen-temp-id)
                         :attachment/type :attachment/textbox
                         :textbox/value "My Text"
                         :coord/x 10
                         :coord/y 45}
    nil))

(defn add
  "Adds an attachment of `type` to the bar containing the chord at `loc`"
  [loc type]
  (case type
    (:bar/start-repeat :bar/end-repeat) (zip/edit (up loc) update type not)
    (zip/edit (up loc) update :bar/attachments #(conj % (make type)))))

(defn move
  "Sets the x-y coords of the symbol with `symbol-id` in bar at `bar-loc`"
  [bar-loc attachment-id [x y]]
  (update-attachment
    #(assoc % :coord/x (floor x) :coord/y (floor y))
    bar-loc attachment-id))

(defn set-value
  "Sets the text value for symbol with `symbol-id` in `bar-loc`"
  [bar-loc attachment-id value]
  (update-attachment
    #(assoc % :textbox/value value)
    bar-loc attachment-id))
