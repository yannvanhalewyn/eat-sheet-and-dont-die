(ns frontend.views.sheet
  (:require [frontend.views.section :as section]
            [frontend.views.editable :as editable]
            [frontend.views.sheet-tools :as sheet-tools]
            [frontend.util.util :refer [presence prevent-default]]
            [re-frame.core :refer [subscribe dispatch]]
            [goog.events.KeyCodes :refer [TAB SPACE ENTER ESC BACKSPACE LEFT RIGHT UP DOWN]])
  (:require-macros [shared.utils :refer [fori]]))

(defn key-down-handler [selected]
  (fn [e]
    (let [code (.-which e)
          shift (if (.-shiftKey e) :shift)
          alt (if (.-altKey e) :alt)
          meta (if (.-metaKey e) :meta)
          pattern (filter identity [alt meta shift code])
          value (.. e -target -value)
          run (fn [rf-event] (.preventDefault e)
                (when selected (dispatch [:sheet/update-chord selected value]))
                (dispatch rf-event))]
      (case pattern
        [ESC] (run [:sheet/deselect])

        [SPACE] (run [:sheet/append :chord])
        [:shift SPACE] (run [:sheet/append :chord])

        [TAB] (run [:sheet/move :right])
        [:shift TAB] (run [:sheet/move :left])

        [ENTER] (run [:sheet/append :bar])
        [:shift ENTER] (run [:sheet/append :row])
        [:meta ENTER] (run [:sheet/append :section])

        [BACKSPACE] (if (empty? value) (run [:sheet/remove :chord]))
        [:meta BACKSPACE] (run [:sheet/remove :bar])
        [:shift BACKSPACE] (run [:sheet/remove :row])
        [:alt :shift BACKSPACE] (run [:sheet/remove :section])

        [LEFT] (run [:sheet/move :left])
        [:meta LEFT] (run [:sheet/move :bar-left])

        [RIGHT] (run [:sheet/move :right])
        [:meta RIGHT] (run [:sheet/move :bar-right])

        [UP] (run [:sheet/move :up])
        [:shift UP] (run [:sheet/move :up])

        [DOWN] (run [:sheet/move :down])
        [:shift DOWN] (run [:sheet/move :down])
        nil))))

(defn component [{:keys [sheet-id sheet deselect append] :as props}]
  (let [sheet @(subscribe [:sub/sheet sheet-id])
        selected @(subscribe [:sub/selected])]
    [:div.u-max-height {:on-click #(dispatch [:sheet/deselect])
                        :on-key-down (key-down-handler selected)}
     (let [title (or (presence (:sheet/title sheet)) "(title)")
           artist (or (presence (:sheet/artist sheet)) "(artist)")]
       [:div
        [editable/component {:on-change #(dispatch [:sheet/set-title %])
                             :value title}
         [:h1 title]]
        [editable/component {:on-change #(dispatch [:sheet/set-artist %])
                             :value artist}
         [:h3.u-margin-top--s artist]]])
     [:div.u-margin-top.sections
      (fori [i section (sort-by :coll/position (:sheet/sections sheet))]
        ^{:key i} [section/component
                   {:section section
                    :selected selected
                    :on-chord-click #(dispatch [:sheet/select-chord %])}])]
     [sheet-tools/component]]))
