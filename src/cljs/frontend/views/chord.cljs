(ns frontend.views.chord
  (:require [reagent.core :as reagent]
            [frontend.util.util :refer [stop-propagation]]
            [frontend.keybindings :as bindings]
            [re-frame.core :refer [dispatch]]
            [clojure.core.match :refer-macros [match]]
            [goog.events :as events]
            [goog.events.EventType :refer [KEYDOWN]]
            [clojure.string :as str]
            [frontend.util.util :as util]))

(defn unicode [{:keys [class code]}]
  [:span {:class class
          :dangerouslySetInnerHTML {:__html (str "&#" code)}}])

(defn flat [] [unicode {:class "accidental--flat" :code "9837"}])
(defn sharp [] [unicode {:class "accidental--sharp" :code "9839"}])
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
   [:span (str/upper-case root)]
   [:span.chord__root-accidental
    (case accidental :flat [flat] :sharp [sharp] nil)]
   (when (= :minor triad) "-")])

(defn- extension
  "Returns a string suitable for our chord symbols font for the
  extension"
  [{:keys [:chord/triad :chord/seventh :chord/extensions]}]
  [:span
   (when (= :augmented triad) "+")
   (when (= :diminished triad)
     (if (= :flat seventh) [half-diminished] [diminished]))
   (when (= :natural seventh) [major-seventh])
   (when (and (= :flat seventh) (not= :diminished triad)
           (not (some #(= ["9" :natural] %) extensions)))
     "7")
   (for [[ext acc] extensions]
     ^{:key (str ext acc)}
     [:span (case acc :flat [flat] :sharp [sharp] "") ext])])

(defn displayed-chord
  "A displayable formatted chord"
  [{:keys [chord on-click]}]
  [:div.chord {:on-click (stop-propagation on-click)}
   (if (:chord/root chord)
     [:span [base chord]
      [:small.chord__extension [extension chord]]
      (when-let [sus (:chord/sus chord)] [:small.chord__extension "sus" sus])
      (when-let [[note acc] (:chord/bass chord)]
        [:small.chord__inversion "/"
         (str/upper-case note)
         [:span.chord__root-accidental (case acc :flat [flat] :sharp [sharp] nil)]])])])

(defn- keydown-handler [e id]
  (.stopPropagation e)
  (when-let [rf-event (bindings/chord-context (util/event->keychord e))]
    (dispatch [:sheet/update-chord id (.. e -target -value) rf-event])))

(defn editable-chord
  "An input box for editing a chord"
  []
  (reagent/create-class
    {:component-did-mount
     (fn [this]
       (doto (reagent/dom-node this) .focus .select
             (events/listen KEYDOWN
               #(keydown-handler % (:db/id (:chord (reagent/props this)))))))
     :reagent-render
     (fn [{:keys [chord]}]
       [:input.chord--editing
        {:type "text"
         :on-click #(.stopPropagation %)
         :on-blur #(dispatch [:sheet/update-chord (:db/id chord) (.. % -target -value)])
         :default-value (:chord/value chord)}])}))
