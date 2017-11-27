(ns frontend.models.bar-attachment-test
  (:require [frontend.models.bar-attachment :as sut]
            [frontend.models.sheet :as sheet]
            [frontend.models.sheet-2 :as sheet-2]
            [datascript.core :as d]
            [clojure.zip :as zip]
            [clojure.set :as set]
            [cljs.test :as t :refer-macros [deftest is]]))

(def BLANK_SHEET
  {:db/id 1
   :sheet/sections {:db/id 2
                    :coll/position 0
                    :section/rows {:db/id 3
                                   :coll/position 0
                                   :row/bars {:db/id 4
                                              :coll/position 0
                                              :bar/chords {:db/id 5
                                                           :coll/position 0
                                                           :chord/value ""}}}}})

(def db (let [conn (d/create-conn sheet-2/schema)]
          (d/transact! conn [BLANK_SHEET])
          @conn))

(def sheet-loc (-> (d/pull db '[*] 1) sheet/zipper (sheet/navigate-to 5)))

(defn submap? [x y]
  "Returns true iff x is a submap of y"
  (set/subset? (set x) (set y)))

(deftest add
  (is (-> (sut/add sheet-loc :bar/end-repeat) zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/end-repeat])))
  (is (-> (sut/add sheet-loc :bar/start-repeat) zip/root
        (get-in [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/start-repeat])))
  (is (submap? {:attachment/type :symbol/segno}
        (-> (sut/add sheet-loc :attachment/segno) zip/node :bar/attachments first)))
  (is (submap? {:attachment/type :symbol/coda}
        (-> (sut/add sheet-loc :attachment/coda) zip/node :bar/attachments first))))

(deftest move
  (let [bar-loc (sut/add sheet-loc :attachment/segno)
        segno-id (-> bar-loc zip/node :bar/attachments first :db/id)]
    (is (submap? {:coord/x 10 :coord/y 20}
          (-> (sut/move bar-loc segno-id [10 20])
            zip/node :bar/attachments first)))))

(deftest set-value
  (let [bar-loc (sut/add sheet-loc :attachment/textbox)
        textbox-id (-> bar-loc zip/node :bar/attachments first :db/id)]
    (is (= "New value"
          (-> (sut/set-value bar-loc textbox-id "New value")
            zip/node :bar/attachments first :textbox/value)))))
