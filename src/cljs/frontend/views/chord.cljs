(ns frontend.views.chord
  (:require [reagent.core :as reagent]
            [frontend.util.util :refer [stop-propagation prevent-default]]
            [re-frame.core :refer [dispatch]]
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
  [{:keys [chord on-click]}]
  [:div.chord {:on-click (stop-propagation on-click)}
   (if (:chord/root chord)
     [:span [base chord]
      [:small.chord__extension [extension chord]]
      (when-let [sus (:chord/sus chord)] [:small.chord__extension "sus" sus])
      (if-let [[note acc] (:chord/bass chord)]
        [:small.chord__inversion "/"
         (str/upper-case note)
         (case acc :flat [flat] :sharp [sharp] nil)])])])

(defn editable-chord
  "An input box for editing a chord"
  []
  (reagent/create-class
    {:component-did-mount
     (fn [this]
       (.focus (reagent/dom-node this))
       (.select (reagent/dom-node this)))
     :reagent-render
     (fn [{:keys [chord]}]
       [:input.chord--editing
        {:type "text"
         :on-click #(.stopPropagation %)
         :on-blur #(dispatch [:sheet/update-chord (:db/id chord) (.. % -target -value)])
         :default-value (:chord/value chord)}])}))
