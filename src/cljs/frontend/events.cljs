(ns frontend.events
  (:require [frontend.selectors :as selectors]
            [frontend.fx :refer [reg-event-db reg-event-fx]]
            [clojure.zip :as zip]
            [frontend.models.sheet :as sheet]))

(defn- update-sheet [db new-sheet]
  (assoc db :db/sheet new-sheet))

(defn- update-sheet-zip [db new-sheet-loc]
  (assoc db
    :db/sheet (zip/root new-sheet-loc)
    :db/selected (-> new-sheet-loc zip/node :db/id)))

(reg-event-fx
  :event/init
  (fn [_]
    {:remote {:get-current-user {:path "/api/me"}}
     :db {:db/sheet {}
          :db/selected nil
          :db/current-user nil
          :db/active-route {:route/handler :route/index}}}))

;; Editor operations
;; =================

(reg-event-db
  :sheet/deselect
  (fn [db] (assoc db :db/selected nil)))

(reg-event-db
  :sheet/select-chord
  (fn [db [_ id]]
    (assoc db :db/selected id)))

(reg-event-db
  :sheet/update-chord
  (fn [db [_ value]]
    (let [new-sheet (zip/root (zip/edit (selectors/current-loc db)
                                assoc :chord/value value))]
      (update-sheet db new-sheet))))

(reg-event-db
  :sheet/append
  (fn [db [_ type]]
    (update-sheet-zip db
      (sheet/append (selectors/current-loc db) type (repeatedly sheet/gen-temp-id)))))

(reg-event-db
  :sheet/move
  (fn [db [_ dir]]
    (if-let [new-sheet (sheet/move (selectors/current-loc db) dir)]
      (update-sheet-zip db new-sheet)
      db)))

(reg-event-db
  :sheet/remove
  (fn [db [_ element]]
    (let [new-sheet (sheet/delete (selectors/current-loc db) element)]
      (update-sheet-zip db new-sheet))))

(reg-event-db
  :sheet/set-title
  (fn [db [_ title]]
    (assoc-in db [:db/sheet :sheet/title] title)))

(reg-event-db
  :sheet/set-artist
  (fn [db [_ artist]]
    (assoc-in db [:db/sheet :sheet/artist] artist)))

(reg-event-db
  :sheet/set-section-title
  (fn [db [_ section title]]
    (let [idx (.indexOf (get-in db [:db/sheet :sheet/sections]) section)]
      (if (>= idx 0)
        (assoc-in db [:db/sheet :sheet/sections idx :section/title] title)
        db))))

;; Remote actions
;; ==============

(reg-event-fx
  :remote/get-sheet
  (fn [{:keys [db]} [_ id]]
    (if-not (= id (:db/id (:db/sheet db)))
      {:remote {:get-sheet {:path (str "/api/sheets/" id)}}})))

(reg-event-fx
  :remote/get-sheets-for-user
  (fn [{:keys [db]} [_ user]]
    (if (empty? (:db/sheets db))
      {:remote {:get-sheets
                {:path (str "/api/users/" (:db/id user) "/sheets")}}})))

(reg-event-db
  :remote/request
  (fn [db event] db))

(reg-event-db
  :remote/success
  (fn [db [_ key response]]
    (case key
      :get-sheet
      (assoc db :db/sheet response)
      :sync-sheet
      (let [tmp-ids (:temp-ids response)]
        (-> (update db :db/sheet sheet/replace-temp-ids tmp-ids)
          (update :db/selected #(if-let [new-id (get tmp-ids %)]
                                  new-id %))))
      :get-current-user
      (assoc db :db/current-user response)
      :get-sheets
      (assoc db :db/sheets response))))

(reg-event-db
  :remote/failure
  (fn [db event]
    (.log js/console "FAILED" event)
    db))

(reg-event-db
  :route/browser-url
  (fn [db [_ route]]
    (assoc db :db/active-route route)))
