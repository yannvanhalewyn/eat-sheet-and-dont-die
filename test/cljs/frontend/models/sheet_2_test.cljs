(ns frontend.models.sheet-2-test
  (:require [frontend.models.sheet-2 :as sut]
            [cljs.test :as t :refer-macros [deftest testing is are]]
            [datascript.core :as d]))

(def BLANK_SHEET
  {:db/id 1
   :sheet/title "Title"
   :sheet/artist "Artist"
   :sheet/sections {:db/id 2
                    :section/title "Intro"
                    :coll/position 0
                    :section/rows {:db/id 3
                                   :coll/position 0
                                   :row/bars {:db/id 4
                                              :coll/position 0
                                              :bar/chords {:db/id 5
                                                           :coll/position 0
                                                           :chord/value ""}}}}})

(def db (let [conn (d/create-conn sut/schema)]
          (d/transact! conn [BLANK_SHEET])
          @conn))

(defn- tx-apply [db tx-fn & args]
  (:db-after (d/with db (binding [sut/*string-tmp-ids* false]
                          (apply tx-fn db args)))))

(deftest update-chord
  (is (= "Ab" (:chord/value (d/entity (tx-apply db sut/update-chord 5 "Ab") 5)))))

(deftest append
  (testing "Append chords at the end"
    (let [db (tx-apply db sut/append :chord 5)]
      (is (= [{:chord/value "" :coll/position 0}
              {:chord/value "" :coll/position 1}]
            (map #(into {} %) (:bar/chords (d/entity db 4)))))))

  (testing "Append chord in between"
    (let [db (-> db
               (tx-apply sut/update-chord 5 "first")
               (tx-apply sut/append :chord 5)
               (tx-apply sut/update-chord 6 "last")
               (tx-apply sut/append :chord 5)
               (tx-apply sut/update-chord 7 "middle"))]
      (is (= [{:chord/value "first" :coll/position 0}
              {:chord/value "last" :coll/position 2}
              {:chord/value "middle" :coll/position 1}]
            (map #(into {} %) (:bar/chords (d/entity db 4)))))))

  (testing "Append bar"
    (is (= [[:db/add 3 :row/bars "new-bar"]
            {:db/id "new-bar" :coll/position 1 :bar/chords "new-chord"}
            {:db/id "new-chord" :chord/value "" :coll/position 0}]
          (sut/append db :bar 5))))

  (testing "Append bar in between"
    (let [db (-> db
               (tx-apply sut/append :bar 5)
               (tx-apply sut/append :bar 5))]
      (is (= [[4 0] [6 2] [8 1]]
            (map #(vector (:db/id %) (:coll/position %)) (:row/bars (d/entity db 3))))))))