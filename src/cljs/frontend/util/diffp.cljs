(ns frontend.util.diffp
  (:require [clojure.set :as set]))

(defn- is-leaf? [x]
  (or (nil? x) (keyword? x) (string? x) (number? x)))

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
        [missing matched1] (split (comp nil? second) matches1)
        [added matched2] (split (comp nil? second) matches2)]
    {:matched (map vec matched1 matched2)
     :missing (map first missing)
     :added (map first added)}))

(defn diffp
  "Takes in two data structures and returns a list of removals,
  additions or updates. Diffp stands for diff-paths."
  ([in1 in2] (diffp in1 in2 identity [] []))
  ([in1 in2 ident-fn] (diffp in1 in2 ident-fn [] []))
  ([in1 in2 ident-fn path ret]
   (if (= in1 in2)
     ret
     (cond

       ;; Leaf case
       (is-leaf? in1)
       (conj ret {:path path :old-value in1 :new-value in2})

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
             matched)))))))
