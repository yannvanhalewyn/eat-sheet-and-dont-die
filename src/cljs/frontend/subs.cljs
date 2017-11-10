(ns frontend.subs
  (:require [re-frame.core :refer [dispatch reg-sub reg-sub-raw]]
            [reagent.ratom :refer [reaction]]))

(defn reg-sub-key [name key]
  (reg-sub name (fn [db] (get db key))))

(reg-sub-key :sub/selected :db/selected)
(reg-sub-key :sub/active-route :db/active-route)

(reg-sub-raw
  :sub/sheet
  (fn [db [_ id]]
    (dispatch [:remote/get-sheet id])
    (reaction (:db/sheet @db))))
