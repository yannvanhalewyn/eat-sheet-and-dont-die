(ns frontend.subs
  (:require [re-frame.core :refer [reg-sub]]))

(defn reg-sub-key [name key]
  (reg-sub name (fn [db] (get db key))))

(reg-sub-key :sub/sheet :db/sheet)
(reg-sub-key :sub/selected :db/selected)
(reg-sub-key :sub/active-route :db/active-route)
