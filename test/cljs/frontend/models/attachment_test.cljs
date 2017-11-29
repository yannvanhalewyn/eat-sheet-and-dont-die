(ns frontend.models.attachment-test
  (:require [frontend.models.attachment :as sut]
            [frontend.models.sheet-zip :as sheet-zip]
            [frontend.models.sheet :as sheet]
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

(def db (let [conn (d/create-conn sheet/schema)]
          (d/transact! conn [BLANK_SHEET])
          @conn))

(defn- tx-apply [db tx-fn & args]
  (:db-after (d/with db (binding [sheet/*string-tmp-ids* false]
                          (apply tx-fn db args)))))

(defn submap? [x y]
  "Returns true iff x is a submap of y"
  (set/subset? (set x) (set y)))

(deftest add
  (is (-> (tx-apply db sut/add 4 :bar/end-repeat) (d/entity 4) :bar/end-repeat))
  (is (-> (tx-apply db sut/add 4 :bar/start-repeat) (d/entity 4) :bar/start-repeat))

  (is (not (-> db
             (tx-apply sut/add 4 :bar/start-repeat)
             (tx-apply sut/add 4 :bar/start-repeat)
             (d/entity 4) :bar/start-repeat)))

  (is (not (-> db
             (tx-apply sut/add 4 :bar/start-repeat)
             (tx-apply sut/add 4 :bar/start-repeat)
             (d/entity 4) :bar/start-repeat)))

  (is (= "1" (-> (tx-apply db sut/add 4 :bar/repeat-cycle) (d/entity 4) :bar/repeat-cycle)))

  (is (submap? {:attachment/type :symbol/segno}
        (-> (tx-apply db sut/add 4 :attachment/segno)
          (d/entity 4) :bar/attachments first)))

  (is (submap? {:attachment/type :symbol/coda}
        (-> (tx-apply db sut/add 4 :attachment/coda)
          (d/entity 4) :bar/attachments first))))

(deftest move
  (let [db (-> (tx-apply db sut/add 4 :attachment/segno))
        att-id (:db/id (first (:bar/attachments (d/entity db 4))))]
    (is (submap? {:coord/x 10 :coord/y 20}
          (-> (tx-apply db sut/move att-id [10 20])
            (d/entity 4) :bar/attachments first)))))

(deftest set-value
  (let [db (tx-apply db sut/add 4 :attachment/textbox)
        textbox-id (:db/id (first (:bar/attachments (d/entity db 4))))]
    (is (= "New value"
          (-> (tx-apply db sut/set-value textbox-id "New value")
            (d/entity 4) :bar/attachments first :textbox/value)))))
