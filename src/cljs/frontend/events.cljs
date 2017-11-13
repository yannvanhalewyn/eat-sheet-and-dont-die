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

(defn reg-events-remote [key]
  (doseq [type #{:request :response :response.failure}]
    (reg-event-db (keyword (name type) (name key))
      (fn [db event] (reducer/app db event)))))

(reg-events-remote :get-sheet)
(reg-events-remote :get-sheets)
(reg-events-remote :sync-sheet)
(reg-events-remote :get-current-user)
(reg-events-remote :create-sheet)
(reg-events-remote :destroy-sheet)

(reg-event-db
  :route/browser-url
  (fn [db event] (reducer/app db event)))
