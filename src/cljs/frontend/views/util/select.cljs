(ns frontend.views.util.select)

(defn component [{:keys [class options selected on-select]}]
  [:select {:on-change #(on-select (.. % -target -value)) :value selected
            :class class}
   (for [i options] ^{:key i} [:option {:value i} i])])
