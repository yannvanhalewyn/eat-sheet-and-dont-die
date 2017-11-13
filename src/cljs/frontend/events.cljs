(ns frontend.events
  (:require [frontend.selectors :as selectors]
            [frontend.fx :refer [reg-event-db reg-event-fx]]
            [frontend.router :as router]
            [frontend.reducer :as reducer]
            [shared.utils :refer [gen-temp-id key-by dissoc-in]]
            [clojure.zip :as zip]
            [frontend.models.sheet :as sheet]))

(reg-event-fx
  :app/init
  (fn [_ event]
    {:remote {:get-current-user {:path "/api/me"}}
     :db (reducer/app nil event)}))

;; Editor operations
;; =================

(reg-event-db
  :sheet/deselect
  (fn [db] (assoc db :db/selected nil)))

(reg-event-db
  :sheet/select-chord
  (fn [db event]
    (reducer/app db event)))

(reg-event-db
  :sheet/update-chord
  (fn [db [_ id value]]
    (let [new-sheet (-> (sheet/zipper (selectors/sheet db))
                      (sheet/navigate-to id)
                      (zip/edit assoc :chord/value value)
                      zip/root)]
      (reducer/app db [:sheet/replace new-sheet]))))

(reg-event-db
  :sheet/append
  (fn [db [_ type]]
    (reducer/app db
      [:sheet/replace-zip
       (sheet/append (selectors/current-loc db) type (repeatedly gen-temp-id))])))

(reg-event-db
  :sheet/move
  (fn [db [_ dir]]
    (if-let [new-sheet (sheet/move (selectors/current-loc db) dir)]
      (reducer/app db [:sheet/replace-zip new-sheet])
      db)))

(reg-event-db
  :sheet/remove
  (fn [db [_ element]]
    (reducer/app db [:sheet/replace-zip
                     (sheet/delete (selectors/current-loc db) element)])))

(reg-event-db
  :sheet/set-title
  (fn [db event] (reducer/app db event)))

(reg-event-db
  :sheet/set-artist
  (fn [db event] (reducer/app db event)))

(reg-event-db
  :sheet/set-section-title
  (fn [db [_ section title]]
    (if-let [new-sheet (-> (selectors/sheet db) sheet/zipper
                         (sheet/navigate-to (:db/id section))
                         (zip/edit assoc :section/title title)
                         zip/root)]
      (reducer/app db [:sheet/replace new-sheet])
      db)))

(reg-event-db
  :sheet/toggle
  (fn [db [_ type]]
    (let [new-sheet (zip/root (sheet/toggle (selectors/current-loc db) type))]
      (reducer/app db [:sheet/replace new-sheet]))))

;; Playlist actions
;; ================

(reg-event-fx
  :playlist/create-sheet
  (fn [db [_ owner-id]]
    {:remote {:create-sheet {:path "/api/sheets"
                             :method :post
                             :params {:owner-id owner-id}}}}))

(reg-event-fx
  :playlist/destroy-sheet
  (fn [db [_ sheet-id]]
    {:remote {:destroy-sheet {:path (str "/api/sheets/" sheet-id)
                              :method :delete}}}))

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
      (-> (assoc-in db [:db/sheets.by-id :db/id] response)
        (assoc
            :db/active-route (router/sheet (:db/id response))
            :db/selected (:db/id (sheet/first-chord response))))
      :destroy-sheet
      (dissoc-in db [:db/sheets.by-id (:removed-id response)]))))

(reg-event-db
  :remote/failure
  (fn [db event]
    (.log js/console "FAILED" event)
    db))

(reg-event-db
  :route/browser-url
  (fn [db event] (reducer/app db event)))
