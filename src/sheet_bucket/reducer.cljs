(ns sheet-bucket.reducer)

(def new-sheet [[[{:id "1" :raw "am"} {:id "2" :raw "edit-me"}]]])
(defn app [state action]
  (case (:type action)
    :init {:name "Intro" :rows new-sheet :current "2"}
    :select-chord (assoc state :current (:id action))
    :update-rows (assoc state :rows (:value action) :current nil)
    state))
