(ns frontend.subs
  (:require [frontend.selectors :as sel]
            [re-frame.core :refer [reg-sub reg-sub-raw]]
            [frontend.http :as http]
            [reagent.ratom :refer [reaction]]))

;; Remote subscriptions
;; ====================
(defn- get-user-sheets [db user]
  (http/request-fx
    {:get-sheets {:path (str "/api/users/" (:db/id user) "/sheets")}}))

(defn- get-sheet-data [db id]
  (http/request-fx {:get-sheet {:path (str "/api/sheets/" id)}}))

;; Re-frame subscriptions
;; ======================

(reg-sub :sub/selection sel/selection)
(reg-sub :sub/active-route sel/active-route)
(reg-sub :sub/current-user sel/current-user)

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
