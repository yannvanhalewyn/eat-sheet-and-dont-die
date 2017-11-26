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

(deftest update-chord
  (is (= "Ab" (:chord/value (d/entity (:db-after (sut/update-chord db 5 "Ab")) 5)))))

(deftest append
  (testing "Append chords at the end"
    (let [db (:db-after (sut/append db :chord 5))]
      (is (= [{:chord/value "" :coll/position 0}
              {:chord/value "" :coll/position 1}]
            (map #(into {} %) (:bar/chords (d/entity db 4)))))))

  (testing "Append chord in between"
    (let [db (-> db
               (sut/update-chord 5 "first") :db-after
               (sut/append :chord 5) :db-after
               (sut/update-chord 6 "last") :db-after
               (sut/append :chord 5) :db-after
               (sut/update-chord 7 "middle") :db-after)]
      (is (= [{:chord/value "first" :coll/position 0}
              {:chord/value "last" :coll/position 2}
              {:chord/value "middle" :coll/position 1}]
            (map #(into {} %) (:bar/chords (d/entity db 4))))))))
