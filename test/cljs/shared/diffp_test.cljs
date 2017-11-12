(ns shared.diffp-test
  (:require [shared.diffp :as sut]
            [cljs.test :refer-macros [deftest is are testing]]))

(deftest diffp
  (are [a b diff] (= diff (sut/diffp a b))
    {:key1 "a"}
    {:key1 "b"}
    [{:path [:key1] :old-value "a" :new-value "b"}]

    {:key1 {:key2 1}}
    {:key1 {:key2 2}}
    [{:path [:key1 :key2] :old-value 1 :new-value 2}]

    {:key1 "a" :key2 "b"}
    {:key1 "a"}
    [{:path [:key2] :old-value "b" :new-value nil}]

    {:key1 "a"}
    {:key1 "a" :key2 "b"}
    [{:path [:key2] :old-value nil :new-value "b"}]

    {:key1 {:key2 [1 2]}}
    {:key1 {:key2 [1 2 3]}}
    [{:path [:key1 :key2] :added 3}]

    {:key1 {:key2 [2 1 3 5]}}
    {:key1 {:key2 [1 2 4 3]}}
    [{:path [:key1 :key2] :removed 5}
     {:path [:key1 :key2] :added 4}])

  (testing "identity fns for children tests"
    (are [a b diff] (= diff (sut/diffp a b #(or (:id %) %)))
      {:key1 {:key2 [{:id "id1" :children ["child1" "child2"]}
                     {:id "id2" :children ["childx" "childy"]}]}}
      {:key1 {:key2 [{:id "id3" :children []}
                     {:id "id1" :children ["child2" "child3"]}]}}
      [{:path [:key1 :key2] :removed {:id "id2" :children ["childx" "childy"]}}
       {:path [:key1 :key2] :added {:id "id3" :children []}}
       {:path [:key1 :key2 "id1" :children] :removed "child1"}
       {:path [:key1 :key2 "id1" :children] :added "child3"}]))

  (testing "boolean key diffs"
    (are [a b diff] (= diff (sut/diffp a b :id))
      {:children [{:id 1 :bar/repeat false}]}
      {:children [{:id 1 :bar/repeat true}]}
      [{:path [:children 1 :bar/repeat] :old-value false :new-value true}])))
