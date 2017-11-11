(ns cards.util)

(defn alert
  "Returns a handler that launches an alert box with all supplied arguments"
  [& messages]
  #(js/alert (apply str messages)))

(defn unparse-chord
  "In order to use the spec generators to generate sheets, we need to
  return chord data to a string"
  [{:keys [:db/id :coll/position :chord/root :chord/triad :chord/seventh :chord/ninth]}]
  {:db/id id
   :coll/position position
   :chord/value (str
                  (first root)
                  (case (second root) :sharp "#" :flat "b" "")
                  (case triad :minor "-" :augmented "+" :diminished "b5" "")
                  (case seventh :major "Maj7" :minor "7" "")
                  (case ninth :natural "9" :flat "b9" :sharp "#9" ""))})

(defn unparse-bar [bar]
  (update bar :bar/chords #(map unparse-chord %)))

(defn unparse-row [row]
  (update row :row/bars #(map unparse-bar %)))

(defn unparse-section [section]
  (update section :section/rows #(map unparse-row %)))

(defn unparse-sheet [sheet]
  (update sheet :sheet/sections #(map unparse-section %)))
