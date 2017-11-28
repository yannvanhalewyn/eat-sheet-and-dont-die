(ns frontend.subs
  (:require [frontend.selectors :as sel]
            [frontend.socket :as sock]
            [re-frame.core :refer [reg-sub reg-sub-raw]]
            [reagent.ratom :refer [reaction]]))

;; Remote subscriptions
;; ====================
(defn- get-user-sheets [user]
  (sock/sock-fx
    {:get-sheets [:sheets/index {:user-id (:db/id user)}]}))

(defn- get-sheet-data [id]
  (sock/sock-fx {:get-sheet [:sheets/show id]}))

;; Re-frame subscriptions
;; ======================

(reg-sub :sub/selection sel/selection)
(reg-sub :sub/active-route sel/active-route)
(reg-sub :sub/current-user sel/current-user)
(reg-sub :sub/current-bar sel/current-bar)

(reg-sub-raw
  :sub/sheets
  (fn [db [_ user]]
    (get-user-sheets user)
    (reaction (sel/sheets @db))))

(reg-sub-raw
  :sub/sheet
  (fn [db [_ id]]
    (get-sheet-data id)
    (reaction (sel/sheet @db))))

(reg-sub :sub/modal :db/modal)
