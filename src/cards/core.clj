(ns cards.core
  (:import java.io.StringWriter))

(defmacro defcard-props
  "A defcard macro to help keep the card's props and prop
  documentation in sync. Will transform into a defcard-rg call with an
  optional message concatinated to a markdown clojure code block of
  the props followed by the component declaration itself."
  ([name [component props]] (list `defcard-props name "" [component props]))
  ([name msg [component props]]
   (list 'devcards.core/defcard-rg name
         (str msg
              "\n```clojure\n"
              (with-out-str (clojure.pprint/pprint props))
              "\n```")
         [component props])))
