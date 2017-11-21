(ns frontend.events
  (:require [frontend.selectors :as selectors]
            [frontend.fx :refer [reg-event-db reg-event-fx]]
            [frontend.router :as router]
            [frontend.reducer :as reducer]
            [frontend.models.sheet :as sheet]
            [frontend.models.bar-attachment :as attachment]
            [shared.utils :as sutil :refer [gen-temp-id key-by dissoc-in]]
            [clojure.zip :as zip]))

(reg-event-db
  :app/init
  (fn [_ event]
    (reducer/app nil event)))

;; Editor operations
;; =================

(reg-event-db
  :sheet/deselect
  (fn [db event] (reducer/app db event)))

(reg-event-db
  :sheet/select
  (fn [db event]
    (reducer/app db event)))

(reg-event-fx
  :sheet/update-chord
  (fn [{:keys [db]} [_ id value next]]
    (let [new-sheet (-> (sheet/zipper (selectors/sheet db))
                      (sheet/navigate-to id)
                      (zip/edit assoc :chord/value value)
                      zip/root)
          new-db (reducer/app db [:sheet/replace new-sheet])]
      (if next {:db new-db :dispatch next} {:db new-db}))))

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
  :sheet/remove-selection
  (fn [db _]
    (reducer/app db [:sheet/replace
                     (sutil/delete-by-id (selectors/sheet db)
                       (:selection/id (selectors/selection db)))])))

(reg-event-db
  :sheet/set-title
  (fn [db event] (reducer/app db event)))

(reg-event-db
  :sheet/set-repeat-cycle
  (fn [db [_ bar-id value]]
    (let [bar-loc (sheet/navigate-to (sheet/zipper (selectors/sheet db)) bar-id)]
      (reducer/app db
        [:sheet/replace
         (if (empty? value)
           (zip/root (zip/edit bar-loc dissoc :bar/repeat-cycle))
           (zip/root (zip/edit bar-loc assoc :bar/repeat-cycle value)))]))))

(reg-event-db
  :sheet/edit-textbox
  (fn [db [_ bar-id textbox-id value]]
    (reducer/app db
      [:sheet/replace
       (-> (sheet/zipper (selectors/sheet db))
         (sheet/navigate-to bar-id)
         (attachment/set-value textbox-id value)
         zip/root)])))

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
  :sheet/add-symbol
  (fn [db [_ type]]
    (if-let [loc (selectors/current-loc db)]
      (let [new-sheet (zip/root (attachment/add loc type))]
        (reducer/app db [:sheet/replace new-sheet]))
      db)))

(reg-event-db
  :sheet/move-symbol
  (fn [db [_ bar-id symbol-id pos]]
    (reducer/app db [:sheet/replace
                     (-> (sheet/zipper (selectors/sheet db))
                       (sheet/navigate-to bar-id)
                       (attachment/move symbol-id pos)
                       zip/root)])))

;; Playlist actions
;; ================

(reg-event-fx
  :playlist/create-sheet
  (fn [db [_ owner-id]]
    {:socket {:create-sheet [:sheets/create owner-id]}}))

(reg-event-fx
  :playlist/destroy-sheet
  (fn [db [_ sheet-id]]
    {:socket {:destroy-sheet [:sheets/destroy sheet-id]}}))

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

(reg-event-fx :chsk/state (fn [_ _] {}))

(reg-event-fx
  :chsk/handshake
  (fn [_ _] {:socket {:get-current-user [:users/me]}}))

(reg-event-db
  :route/browser-url
  (fn [db event] (reducer/app db event)))
