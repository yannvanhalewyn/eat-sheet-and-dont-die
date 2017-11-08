(ns sheet-bucket.components.chord
  (:require [reagent.core :as reagent]
            [sheet-bucket.util.util :refer [stop-propagation prevent-default]]
            [goog.events.KeyCodes :refer [TAB SPACE ENTER ESC BACKSPACE LEFT RIGHT UP DOWN]]
            [clojure.core.match :refer-macros [match]]
            [clojure.string :as str]))

(defn flat [] [:i.music-notation "L"])
(defn sharp [] [:i.music-notation "K"])
(defn diminished [] [:span "\u006F"])
(defn half-diminished [] [:span "\u00F8"])
(defn major-seventh [] [:span "\u0394"])

(defn- Ninth [type]
  [:span
   (case type :sharp [sharp] :flat [flat] [:span ""])
   (when type "9")])

(defn- base
  "Returns a string suitable for the chord symbols font for the base
  root and triad"
  [{[root accidental] :chord/root triad :chord/triad}]
  [:span
   (str/upper-case root)
   (condp = accidental :flat [flat] :sharp [sharp] nil)
   (when (= :minor triad) "-")])

(defn- extension
  "Returns a string suitable for our chord symbols font for the
  extension"
  [{:keys [:chord/root :chord/triad :chord/seventh :chord/ninth]}]
  [:span
   (match [triad seventh ninth]
     [:diminished :minor _] [half-diminished]
     [_ :major NINTH] [:span [major-seventh] [Ninth NINTH]]
     [_ :minor NINTH] [:span [:span (if (= :natural NINTH) "9" [:span "7" [Ninth NINTH]])]]
     [:diminished :diminished NINTH] [:span [diminished] [Ninth NINTH]]
     [:diminished _ _] [:span [flat] "5"]
     [:augmented _ _] "+"
     [_ _ :natural] "9"
     [_ _ :sharp] [:span [sharp] "9"]
     [_ _ :flat] [:span [flat] "9"]
     :else nil)])

(defn displayed-chord
  "A displayable formatted chord"
  [{:keys [chord on-click] :as props}]
  [:div.chord {:on-click (stop-propagation on-click)}
   (if (:chord/root chord)
     [:span [base chord]
      [:small.chord__extension [extension chord]]])])

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
     (fn [this]
       (.focus (reagent/dom-node this))
       (.select (reagent/dom-node this)))
     :reagent-render
     (fn [{:keys [update-chord chord] :as props}]
       [:input.chord--editing
        {:type "text"
         :on-click #(.stopPropagation %)
         :on-blur #(update-chord  (.. % -target -value))
         :on-key-down (key-down-handler props)
         :default-value (:chord/value chord)}])}))
