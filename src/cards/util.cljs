(ns cards.util)

(defn alert
  "Returns a handler that launches an alert box with all supplied arguments"
  [& messages]
  #(js/alert (apply str messages)))
