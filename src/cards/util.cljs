(ns cards.util)

(defn alert
  "Returns a handler that launches an alert box with all supplied arguments"
  [& messages]
  #(js/alert (apply str messages)))

(defn unparse-chord
  "In order to use the spec generators to generate sheets, we need to
  return chord data to a string"
  [{:keys [id root triad seventh ninth]}]
  {:id id
   :raw (str
         (first root)
         (case (second root) :sharp "#" :flat "b" "")
         (case triad :minor "-" :augmented "+" :diminished "b5" "")
         (case seventh :major "Maj7" :minor "7" "")
         (case ninth :natural "9" :flat "b9" :sharp "#9" ""))})

(defn unparse-rows [rows]
  (for [row rows]
    (for [bar row] (map unparse-chord bar))))

(defn unparse-sections [sections]
  (map #(update % :rows unparse-rows) sections))
