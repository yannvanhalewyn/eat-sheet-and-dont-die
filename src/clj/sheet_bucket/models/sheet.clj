(ns sheet-bucket.models.sheet
  (:require [clojure.walk :refer [postwalk]]
            [datomic.api :as d]))

(def BLANK_SHEET
  {:db/id "new-sheet"
   :sheet/title "Title"
   :sheet/artist "Artist"
   :sheet/sections {:section/title "Intro"
                    :coll/position 0
                    :section/rows {:coll/position 0
                                   :row/bars {:coll/position 0
                                              :bar/chords {:coll/position 0
                                                           :chord/value ""}}}}})

(defn- resolve-enums [keys db]
  (fn [node]
    (if (and (coll? node) (keys (first node)))
      (update node 1 #(:db/ident (d/entity db (:db/id %))))
      node)))

(defn find [db-conn sheet-id]
  (let [db (d/db db-conn)]
    (postwalk
      (resolve-enums #{:attachment/type} db)
      (d/pull db '[*] sheet-id))))

(defn create! [db-conn owner-id]
  (let [res (d/transact db-conn
              [BLANK_SHEET {:db/id owner-id :playlist/sheets "new-sheet"}])]
    (d/pull (:db-after @res) '[*] (get-in @res [:tempids "new-sheet"]))))
