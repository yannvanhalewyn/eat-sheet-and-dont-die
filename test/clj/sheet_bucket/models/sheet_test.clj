(ns sheet-bucket.models.sheet-test
  (:require [sheet-bucket.models.sheet :as sut]
            [shared.diffp :refer [diffp]]
            [clojure.test :refer :all]))

(def sheet
  {:db/id 1
   :sheet/title "Title"
   :sheet/artist "Artist"
   :sheet/sections [{:db/id 2
                     :section/name "Intro"
                     :section/rows [{:db/id 3
                                     :row/bars [{:db/id 4
                                                 :bar/chords [{:db/id 5
                                                               :chord/value "A"}
                                                              {:db/id 6
                                                               :chord/value "B"}]}]}]}]})

(defn- test [old new]
  (sut/diff->tx (diffp sheet new :db/id) (:db/id old)))

(deftest diff->tx
  (is (= [{:db/id 1 :sheet/title "New title"}]
        (test sheet (assoc sheet :sheet/title "New title"))))
  (is (= [{:db/id 1 :sheet/artist "New artist"} {:db/id 1 :sheet/title "New title"}]
        (test sheet (assoc sheet :sheet/title "New title" :sheet/artist "New artist"))))
  (is (= [{:db/id 1
           :sheet/sections {:db/id 2
                            :section/rows {:db/id 3
                                           :row/bars {:db/id 4
                                                      :bar/chords {:db/id 5
                                                                   :chord/value "Ab"}}}}}]
        (test sheet
          (assoc-in sheet [:sheet/sections 0 :section/rows 0 :row/bars 0 :bar/chords 0
                           :chord/value] "Ab"))))
  (testing "It handles values set to a boolean false"
    (is (= [{:db/id 1
             :sheet/sections {:db/id 2
                              :section/rows {:db/id 3
                                             :row/bars {:db/id 4
                                                        :bar/end-repeat false}}}}]
          (test sheet
            (assoc-in sheet [:sheet/sections 0 :section/rows 0 :row/bars 0
                             :bar/end-repeat] false)))))
  (testing "It handles retractions"
    (is (= [[:db.fn/retractEntity 2]]
          (test sheet (dissoc sheet :sheet/sections))))))
