(ns frontend.models.sheet-2
  (:require [datascript.core :as d]))

(def children-type {:db/valueType :db.type/ref
                    :db/cardinality :db.cardinality/many
                    :db/isComponent true})

(def schema
  {:sheet/sections children-type
   :section/rows children-type
   :row/bars children-type
   :bar/chords children-type})

(defn- transact!
  "An immutable version of transact, will return transaction data with
  :db-after that can be used to replace the previous db. "
  [db tx-data]
  (d/with db tx-data))

(def q-one (comp ffirst d/q))

(defn update-chord
  "Returns a new db where chord `chord-id` has the new `value`"
  [db chord-id value]
  [[:db/add chord-id :chord/value value]] )

(defn pull-all [db]
  (d/q '[:find [(pull ?sheet [*])]
         :where [?sheet :sheet/title]] db))

;; Append
;; ======

(defmulti append* (fn [_ t _] t))

(defn append [db type chord-id]
  (if-let [new-db (append* db type chord-id)]
    new-db
    (do (.error js/console "Could not append" type "after" chord-id)
        db)))

(defmethod append* :default [db t _]
  (.error js/console "No append fn defined for" t)
  db)

(defmethod append* :chord
  [db _ cur-chord-id]
  (when-let [bar (q-one '[:find (pull ?bar [*])
                          :in $ ?chord
                          :where [?bar :bar/chords ?chord]]
                   db cur-chord-id)]
    (let [pos (inc (:coll/position (d/entity db cur-chord-id)))
          tempid (str (d/tempid db))
          chord-pos-txes (map (fn [{:keys [db/id coll/position]}]
                                [:db/add id :coll/position
                                 (if (< position pos) (or position 0) (inc position))])
                           (:bar/chords bar))]
      (into [[:db/add (:db/id bar) :bar/chords tempid]
             {:db/id tempid :coll/position pos :chord/value ""}]
        chord-pos-txes))))

(defmethod append* :bar
  [db _ cur-chord-id]
  (when-let [[bar row-id] (d/q '[:find [(pull ?bar [*]) ?row]
                                 :in $ ?chord
                                 :where
                                 [?bar :bar/chords ?chord]
                                 [?row :row/bars ?bar]
                                 [?row :row/bars ?next-bars]]
                            db cur-chord-id)]
    (let [pos (inc (:coll/position bar))]
      [[:db/add row-id :row/bars "new-bar"]
       {:db/id "new-bar" :coll/position pos :bar/chords "new-chord"}
       {:db/id "new-chord" :coll/position 0 :chord/value ""}])))
