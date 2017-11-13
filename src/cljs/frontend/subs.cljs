(ns frontend.subs
  (:require [frontend.selectors :as sel]
            [re-frame.core :refer [reg-sub reg-sub-raw]]
            [frontend.http :as http]
            [reagent.ratom :refer [reaction]]))

;; Remote subscriptions
;; ====================
(defn- get-user-sheets [db user]
  (if (empty? (:db/sheets db))
    (http/request-fx
      {:get-sheets {:path (str "/api/users/" (:db/id user) "/sheets")}})))

(defn- get-sheet-data [db id]
  (if-not (= id (:db/id (:db/sheet db)))
    (http/request-fx {:get-sheet {:path (str "/api/sheets/" id)}})))

(defn- reg-sub-key [name key]
  (reg-sub name (fn [db] (get db key))))

;; Re-frame subscriptions
;; ======================

(reg-sub-key :sub/selected :db/selected)
(reg-sub-key :sub/active-route :db/active-route)
(reg-sub-key :sub/current-user :db/current-user)

(reg-sub-raw
  :sub/sheets
  (fn [db [_ user]]
    (get-user-sheets db user)
    (reaction (vals (:db/sheets.by-id @db)))))

(reg-sub-raw
  :sub/sheet
  (fn [db [_ id]]
    (get-sheet-data db id)
    (reaction (sel/sheet @db))))
