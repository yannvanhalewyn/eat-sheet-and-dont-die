(ns frontend.events
  (:require [frontend.selectors :as selectors]
            [frontend.fx :refer [reg-event-db reg-event-fx]]
            [frontend.router :as router]
            [shared.utils :refer [gen-temp-id key-by]]
            [clojure.zip :as zip]
            [frontend.models.sheet :as sheet]))

(defn- update-sheet [db new-sheet]
  (assoc-in db [:db/sheets.by-id (:db/id new-sheet)] new-sheet))

(defn- update-sheet-zip [db new-sheet-loc]
  (-> (update-sheet db (zip/root new-sheet-loc))
    (assoc :db/selected (-> new-sheet-loc zip/node :db/id))))

(reg-event-fx
  :event/init
  (fn [_]
    {:remote {:get-current-user {:path "/api/me"}}
     :db {:db/sheets.by-id {}
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
  (fn [db [_ id value]]
    (let [new-sheet (-> (sheet/zipper (selectors/sheet db))
                      (sheet/navigate-to id)
                      (zip/edit assoc :chord/value value)
                      zip/root)]
      (update-sheet db new-sheet))))

(reg-event-db
  :sheet/append
  (fn [db [_ type]]
    (update-sheet-zip db
      (sheet/append (selectors/current-loc db) type (repeatedly gen-temp-id)))))

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
    (update-sheet db (assoc (selectors/sheet db) :sheet/title title))))

(reg-event-db
  :sheet/set-artist
  (fn [db [_ artist]]
    (update-sheet db (assoc (selectors/sheet db) :sheet/artist artist))))

(reg-event-db
  :sheet/set-section-title
  (fn [db [_ section title]]
    (let [idx (.indexOf (:sheet/sections (selectors/sheet db)) section)]
      (if (>= idx 0)
        (update-sheet db
          (assoc-in (selectors/sheet db) [:sheet/sections idx :section/title] title))
        db))))

(reg-event-db
  :sheet/toggle
  (fn [db [_ type]]
    (let [new-sheet (zip/root (sheet/toggle (selectors/current-loc db) type))]
      (update-sheet db new-sheet))))

;; Playlist actions
;; ================

(reg-event-fx
  :playlist/create-sheet
  (fn [db [_ owner-id]]
    {:remote {:create-sheet {:path "/api/sheets"
                             :method :post
                             :params {:owner-id owner-id}}}}))

;; Remote actions
;; ==============

(reg-event-db
  :remote/request
  (fn [db event] db))

(reg-event-db
  :remote/success
  (fn [db [_ key response]]
    (case key
      :get-sheet
      (assoc-in db [:db/sheets.by-id (:db/id response)] response)
      :sync-sheet
      (let [tmp-ids (:temp-ids response)]
        (-> (update-in db [:db/sheets.by-id (selectors/current-sheet-id db)]
              sheet/replace-temp-ids tmp-ids)
          (update :db/selected #(if-let [new-id (get tmp-ids %)]
                                  new-id %))))
      :get-current-user
      (assoc db :db/current-user response)
      :get-sheets
      (assoc db :db/sheets.by-id (key-by :db/id response))
      :create-sheet
      (assoc db :db/active-route (router/sheet (:id response))))))

(reg-event-db
  :remote/failure
  (fn [db event]
    (.log js/console "FAILED" event)
    db))

(reg-event-db
  :route/browser-url
  (fn [db [_ route]]
    (assoc db :db/active-route route)))
