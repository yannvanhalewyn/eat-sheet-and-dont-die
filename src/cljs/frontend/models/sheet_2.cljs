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

;; Hack - Datomic can handle string temp ids, datascript can't. Use
;; the useful string temp ids for transactions send to the backend,
;; but test them in cljs with neg-numbers so we can transact them to
;; datascript.
(def ^:dynamic *string-tmp-ids* true)

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
          tempid (if *string-tmp-ids* "new-chord" -1)
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
                                 [?row :row/bars ?bar]]
                            db cur-chord-id)]
    (let [next-bars (d/q '[:find ?bar ?pos
                           :in $ ?row ?cur-bar
                           :where
                           [?cur-bar :coll/position ?cur-pos]
                           [?row :row/bars ?bar]
                           [?bar :coll/position ?pos]
                           [(> ?pos ?cur-pos)]]
                      db row-id (:db/id bar))
          bar-pos-txes
          (map (fn [[id position]]
                 [:db/add id :coll/position (inc position)])
            next-bars)
          pos (inc (:coll/position bar))
          new-bar-id (if *string-tmp-ids* "new-bar" -1)
          new-chord-id (if *string-tmp-ids* "new-chord" -2)]
      (into
        [[:db/add row-id :row/bars new-bar-id]
         {:db/id new-bar-id :coll/position pos :bar/chords new-chord-id}
         {:db/id new-chord-id :coll/position 0 :chord/value ""}]
        bar-pos-txes))))
