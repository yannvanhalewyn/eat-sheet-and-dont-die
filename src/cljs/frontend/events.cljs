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
    (let [id (sheet/gen-temp-id)]
      {:db {:db/sheet (sheet/new-sheet id) :db/selected id}
       :remote {:get-sheet {:path "/api/sheets"}}})))

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
    (let [new-sheet (sheet/append (selectors/current-loc db) type (sheet/gen-temp-id))]
      (update-sheet-zip db new-sheet))))

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
  :remote/request
  (fn [db event] db))

(reg-event-db
  :remote/success
  (fn [db [_ key response]]
    (case key
      :get-sheet
      (assoc db :db/sheet response))))
