(ns frontend.keybindings
  (:require [goog.events.KeyCodes
             :refer [TAB SPACE ENTER ESC BACKSPACE LEFT RIGHT UP DOWN R]]))

(def chord-context
  {[ESC]                               [:sheet/deselect]
   [SPACE]                             [:sheet/append :chord]
   [:shift SPACE]                      [:sheet/append :chord]
   [TAB]                               [:sheet/move :right]
   [:shift TAB]                        [:sheet/move :left]
   [ENTER]                             [:sheet/append :bar]
   [:shift ENTER]                      [:sheet/append :row]
   [:meta ENTER]                       [:sheet/append :section]
   [:meta BACKSPACE]                   [:sheet/remove :chord]
   [:meta :shift BACKSPACE]            [:sheet/remove :bar]
   [:alt :meta :shift BACKSPACE]       [:sheet/remove :row]
   [:ctrl :alt :meta :shift BACKSPACE] [:sheet/remove :section]
   [LEFT]                              [:sheet/move :left]
   [RIGHT]                             [:sheet/move :right]
   [UP]                                [:sheet/move :up]
   [DOWN]                              [:sheet/move :down]
   [:meta LEFT]                        [:sheet/move :bar-left]
   [:meta RIGHT]                       [:sheet/move :bar-right]
   [:meta UP]                          [:sheet/move :up]
   [:meta DOWN]                        [:sheet/move :down]

   ;; Bar edits
   [R]                                 [:sheet/add-bar-attachment :bar/end-repeat]
   [:shift R]                          [:sheet/add-bar-attachment :bar/start-repeat]})

(def global-context
  {[ESC]       [:sheet/deselect]
   [BACKSPACE] [:sheet/remove-selection]})
