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

(defn- move-next-children-right
  "Takes a db, parent and current child, and returns the txes to
  increment the :coll/position of every child to the right from
  current child."
  [db parent-id current-child-id children-key]
  (let [next-children (d/q '[:find ?child ?pos
                             :in $ ?parent ?cur-child ?children-key
                             :where
                             ;; Find position of current child
                             [?cur-child :coll/position ?cur-pos]
                             ;; Find all children to the right of that position
                             [?parent ?children-key ?child]
                             [?child :coll/position ?pos]
                             [(> ?pos ?cur-pos)]]
                        db parent-id current-child-id children-key)]
    (map (fn [[id position]]
           [:db/add id :coll/position (inc position)])
      next-children)))

(defmethod append* :chord
  [db _ cur-chord-id]
  (when-let [bar (d/q '[:find (pull ?bar [*]) .
                        :in $ ?chord
                        :where [?bar :bar/chords ?chord]]
                   db cur-chord-id)]
    (let [pos (inc (:coll/position (d/entity db cur-chord-id)))
          tempid (if *string-tmp-ids* "new-chord" -1)]
      (concat
        [[:db/add (:db/id bar) :bar/chords tempid]
         {:db/id tempid :coll/position pos :chord/value ""}]
        (move-next-children-right db (:db/id bar) cur-chord-id :bar/chords)))))

(defmethod append* :bar
  [db _ cur-chord-id]
  (when-let [[bar row-id] (d/q '[:find [(pull ?bar [*]) ?row]
                                 :in $ ?chord
                                 :where
                                 [?bar :bar/chords ?chord]
                                 [?row :row/bars ?bar]]
                            db cur-chord-id)]
    (let [pos (inc (:coll/position bar))
          new-bar-id (if *string-tmp-ids* "new-bar" -1)
          new-chord-id (if *string-tmp-ids* "new-chord" -2)]
      (concat
        [[:db/add row-id :row/bars new-bar-id]
         {:db/id new-bar-id :coll/position pos :bar/chords new-chord-id}
         {:db/id new-chord-id :coll/position 0 :chord/value ""}]
        (move-next-children-right db row-id (:db/id bar) :row/bars)))))

(defmethod append* :row
  [db _ cur-chord-id]
  (when-let [[row section-id] (d/q '[:find [(pull ?row [*]) ?section]
                                     :in $ ?chord
                                     :where
                                     [?section :section/rows ?row]
                                     [?row :row/bars ?bar]
                                     [?bar :bar/chords ?chord]]
                                db cur-chord-id)]
    (let [pos (inc (:coll/position row))
          new-row-id (if *string-tmp-ids* "new-row" -1)
          new-bar-id (if *string-tmp-ids* "new-bar" -2)
          new-chord-id (if *string-tmp-ids* "new-chord" -3)]
      (concat
        [[:db/add section-id :section/rows new-row-id]
         {:db/id new-row-id :coll/position pos :row/bars new-bar-id}
         {:db/id new-bar-id :coll/position 0 :bar/chords new-chord-id}
         {:db/id new-chord-id :coll/position 0 :chord/value ""}]
        (move-next-children-right db section-id (:db/id row) :section/rows)))))

(defmethod append* :section
  [db _ cur-chord-id]
  (when-let [[section sheet-id] (d/q '[:find [(pull ?section [*]) ?sheet]
                                       :in $ ?chord
                                       :where
                                       [?sheet :sheet/sections ?section]
                                       [?section :section/rows ?row]
                                       [?row :row/bars ?bar]
                                       [?bar :bar/chords ?chord]]
                                  db cur-chord-id)]
    (let [pos (inc (:coll/position section))
          new-section-id (if *string-tmp-ids* "new-section" -1)
          new-row-id (if *string-tmp-ids* "new-row" -2)
          new-bar-id (if *string-tmp-ids* "new-bar" -3)
          new-chord-id (if *string-tmp-ids* "new-chord" -4)]
      (concat
        [[:db/add sheet-id :sheet/sections new-section-id]
         {:db/id new-section-id
          :coll/position pos
          :section/title "Section"
          :section/rows new-row-id}
         {:db/id new-row-id :coll/position 0 :row/bars new-bar-id}
         {:db/id new-bar-id :coll/position 0 :bar/chords new-chord-id}
         {:db/id new-chord-id :coll/position 0 :chord/value ""}]
        (move-next-children-right db sheet-id (:db/id section) :sheet/sections)))))
