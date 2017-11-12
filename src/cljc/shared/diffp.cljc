(ns shared.diffp
  (:require [clojure.set :as set]))

(defn- find-match [elem coll ident-fn]
  (loop [[test & others] coll]
    (if (nil? test)
      [elem nil]
      (if (= (ident-fn elem) (ident-fn test))
        [elem test]
        (recur others)))))

(defn- split [pred coll]
  (let [{matched true unmatched false} (group-by pred coll)]
    [matched unmatched]))

(defn- find-matches
  "Splits up two collections into matching elements, missing elements
  (in coll1 but not 2) and added elements (in coll2 but not 1) without
  regard to element order.
  Ex:
    (find-matches [1 2 3 4] [5 3 1 2] identity)
    ;; => {:matched ([1 1] [2 2] [3 3]), :missing (4), :added (5)}"
  [coll1 coll2 ident-fn]
  (let [matches1 (map #(find-match % coll2 ident-fn) coll1)
        matches2 (map #(find-match % coll1 ident-fn) coll2)
        [missing matched] (split (comp nil? second) matches1)
        added (filter (comp nil? second) matches2)]
    {:matched matched
     :missing (map first missing)
     :added (map first added)}))

(defn diffp
  "Takes in two data structures and returns a list of removals,
  additions or updates. Diffp stands for diff-paths.

  Examples:
  ```
  (diffp {:name \"Fred\" :age 22} {:name \"Fred\" :age 23})
  ;; => ({:path [:age], :old-value 22, :new-value 23})

  (diffp {:name \"Fred\"} {:name \"Fred\" :age 22})
  ;; => ({:path [:age], :old-value 22, :new-value 23})
  ```

  diffp will also find matches in collections, in case an element
  moved. Notice how the diff only says \"Rose\" was removed, even
  though the other children moved in the collection:

  ```
  (diffp {:name \"Fred\" :children [\"Mary\" \"Rose\" \"Elsa\"]}
         {:name \"Fred\" :children [\"Elsa\" \"Mary\"]})
  ;; => ({:path [:children], :removed \"Rose\"})
  ```

  You can pass in a custom identity function to match children in a
  list, eg: `:db/id`
  "
  ([in1 in2] (diffp in1 in2 identity [] []))
  ([in1 in2 ident-fn] (diffp in1 in2 ident-fn [] []))
  ([in1 in2 ident-fn path ret]
   (if (= in1 in2)
     ret
     (cond
       ;; Map case
       (map? in1)
       (let [keys-in1 (set (keys in1))
             keys-in2 (set (keys in2))]
         (mapcat #(diffp (% in1) (% in2) ident-fn (conj path %) ret) (into keys-in1 keys-in2)))

       ;; Sequence case
       (sequential? in1)
       (let [{:keys [matched missing added]} (find-matches in1 in2 ident-fn)
             ret (concat ret)]
         (concat
           (map (fn [e] {:path path :removed e}) missing)
           (map (fn [e] {:path path :added e}) added)
           (mapcat (fn [[a b]] (diffp a b ident-fn (conj path (ident-fn a)) ret))
             matched)))

       ;; Leaf case
       :leaf
       (conj ret {:path path :old-value in1 :new-value in2})))))
