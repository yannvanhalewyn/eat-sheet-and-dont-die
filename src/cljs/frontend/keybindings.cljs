(ns frontend.keybindings
  (:require [goog.events.KeyCodes
             :refer [TAB SPACE ENTER ESC BACKSPACE LEFT RIGHT UP DOWN]]))

(def keyboard-shortcuts
  {[ESC]                   [:sheet/deselect]
   [SPACE]                 [:sheet/append :chord]
   [:shift SPACE]          [:sheet/append :chord]
   [TAB]                   [:sheet/move :right]
   [:shift TAB]            [:sheet/move :left]
   [ENTER]                 [:sheet/append :bar]
   [:shift ENTER]          [:sheet/append :row]
   [:meta ENTER]           [:sheet/append :section]
   [BACKSPACE]             [:sheet/remove-selection]
   [:meta BACKSPACE]       [:sheet/remove :bar]
   [:shift BACKSPACE]      [:sheet/remove :row]
   [:alt :shift BACKSPACE] [:sheet/remove :section]
   [LEFT]                  [:sheet/move :left]
   [:meta LEFT]            [:sheet/move :bar-left]
   [RIGHT]                 [:sheet/move :right]
   [:meta RIGHT]           [:sheet/move :bar-right]
   [UP]                    [:sheet/move :up]
   [:shift UP]             [:sheet/move :up]
   [DOWN]                  [:sheet/move :down]
   [:shift DOWN]           [:sheet/move :down]})
