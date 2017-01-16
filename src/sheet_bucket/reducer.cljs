(ns sheet-bucket.reducer)

(def new-sheet
  [[;; Sections
    [[;; Rows
      [[;; Bars
        [[{:id "1" :raw "am"} {:id "2" :raw "edit-me"}]]]]]
     {:name "Intro"}]]
   {:title "Song name" :artist "Artist"}])

(defn app [state action]
  (case (:type action)
    :init {:sheet new-sheet :selected "2"}
    :select-chord (assoc state :selected (:id action))
    :sheet/update (assoc state :sheet (:value action) :selected nil)
    state))
