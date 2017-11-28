(ns frontend.models.bar-attachment
  (:require [frontend.models.sheet :as sheet]
            [datascript.core :as d]))

;; Constants
;; =========

(def height 30)
(def segno-svg-ratio (/ 54 80))
(def segno-width (* height segno-svg-ratio))
(def coda-width height)

;; Helpers
;; =======

(def floor (.-floor js/Math))

(defn- make [type]
  (case type
    :attachment/coda {:attachment/type :symbol/coda
                      :coord/x (floor (/ coda-width 2))
                      :coord/y (floor (- -4 height))}
    :attachment/segno {:attachment/type :symbol/segno
                       :coord/x (floor (/ (- segno-width) 2))
                       :coord/y (floor (- -4 height))}
    :attachment/textbox {:attachment/type :attachment/textbox
                         :textbox/value ""
                         :coord/x 10
                         :coord/y 45}
    nil))

(defn add
  "Adds an attachment of `type` to the given bar."
  [db bar-id type]
  (case type
    (:bar/start-repeat :bar/end-repeat) (if (get (d/entity db bar-id) type)
                                          [[:db/retract bar-id type true]]
                                          [[:db/add bar-id type true]])
    :bar/repeat-cycle [[:db/add bar-id :bar/repeat-cycle "1"]]
    (let [tmpid (if sheet/*string-tmp-ids* "new-attachment" -1)]
      [[:db/add bar-id :bar/attachments tmpid]
       (assoc (make type) :db/id tmpid)])))

(defn move
  "Sets the x-y coords of the attachment with `att-id`"
  [db att-id [x y]]
  [[:db/add att-id :coord/x x]
   [:db/add att-id :coord/y y]])

(defn set-value
  "Sets the text value for symbol with `att-id`"
  [db att-id value]
  [[:db/add att-id :textbox/value value]])

(defn set-repeat-cycle
  "Sets the repeat cycle number for bar"
  [db bar-id value]
  (if (empty? value)
    [[:db/retract bar-id :bar/repeat-cycle
      (:bar/repeat-cycle (d/entity db bar-id))]]
    [[:db/add bar-id :bar/repeat-cycle value]]))
