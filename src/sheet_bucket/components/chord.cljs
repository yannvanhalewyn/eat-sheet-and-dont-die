(ns sheet-bucket.components.chord
  (:require [reagent.core :as reagent]
            [sheet-bucket.util.util :refer [stop-propagation prevent-default]]
            [cljs.core.match :refer [match]]
            [goog.events.KeyCodes :refer [TAB SPACE ENTER ESC BACKSPACE LEFT RIGHT UP DOWN]]
            [clojure.string :as str]))

(def style
  {:chord {:font-family "NashvillechordSymbols"
           :cursor "pointer"
           :font-size "1.7em"}
   :extension {:font-size "0.7em"
               :position "relative"
               :bottom "9px" :left "3px"}})

(defn- base
  "Returns a string suitable for the chord symbols font for the base
  root and triad"
  [{[root accidental] :root triad :triad}]
  (str (str/upper-case root)
       (condp = accidental :flat "@" :sharp "#" "")
       (when (or (= :minor triad) (= :diminished triad)) "-")))

(defn- extension
  "Returns a string suitable for our chord symbols font for the
  extension"
  [{:keys [root triad seventh ninth]}]
  (str
   (when (= triad :augmented) "+")
   (when (= seventh :major) "y")
   (when (and seventh (not ninth)) "7")
   (case ninth
     :natural "9"
     :sharp "#9"
     :flat "@9"
     "")
   (when (= triad :diminished) "b5")))

(defn displayed-chord
  "A displayable formatted chord"
  [{[root accidental] :root :as props}]
  [:span {:style (:chord style) :on-click (stop-propagation (:on-click props))}
   (if (:root props)
     [:span
      [:span (base props)]
      [:span {:style (:extension style)} (extension props)]])])

(defn key-down-handler [{:keys [append remove move deselect update-chord]}]
  (fn [e]
    (let [code (.-which e)
          shift (if (.-shiftKey e) :shift)
          alt (if (.-altKey e) :alt)
          meta (if (.-metaKey e) :meta)
          pattern (filter identity [alt meta shift code])
          value (.. e -target -value)
          run (fn [handler] (.preventDefault e) (update-chord value) (handler))]
      (case pattern
        [ESC] (run deselect)

        [SPACE] (run #(append :chord))
        [:shift SPACE] (run #(append :chord))

        [TAB] (run #(move :right))
        [:shift TAB] (run #(move :left))

        [ENTER] (run #(append :bar))
        [:shift ENTER] (run #(append :row))
        [:meta ENTER] (run #(append :section))

        [BACKSPACE] (if (empty? value) (run #(remove :chord)))
        [:meta BACKSPACE] (run #(remove :bar))
        [:shift BACKSPACE] (run #(remove :row))
        [:alt :shift BACKSPACE] (run #(remove :section))

        [LEFT] (run #(move :left))
        [:meta LEFT] (run #(move :bar-left))

        [RIGHT] (run #(move :right))
        [:meta RIGHT] (run #(move :bar-right))

        [UP] (run #(move :up))
        [:shift UP] (run #(move :up))

        [DOWN] (run #(move :down))
        [:shift DOWN] (run #(move :down))
        nil))))

(defn editable-chord
  "An input box for editing a chord"
  []
  (reagent/create-class
   {:component-did-mount
    (fn [this] (.focus (reagent/dom-node this)))
    :reagent-render
    (fn [{:keys [update-chord] :as props}]
      [:input {:type "text"
               :style {:width "100%"}
               :on-blur #(update-chord  (.. % -target -value))
               :on-key-down (key-down-handler props)
               :default-value (:text props)}])}))
