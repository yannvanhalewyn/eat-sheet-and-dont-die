(ns frontend.fx
  (:require [frontend.socket :as sock]
            [frontend.selectors :as sel]
            [frontend.router :as router]
            [frontend.specs :as specs]
            [shared.diffp :refer [diffp]]
            [shared.datsync :as sync]
            [cljs.spec.alpha :as s]
            [re-frame.core :as rf]
            [goog.string :refer [format]]
            [datascript.core :as d]
            [frontend.models.sheet-2 :as sheet-2]))

(rf/reg-fx :socket sock/sock-fx)

(rf/reg-fx :datsync
  (fn [tx] (sock/sock-fx {:datsync [:tx/sync (sync/normalize-tx tx)]})))

;; Development interceptors
;; ========================

(def spec-checker
  (rf/after
    (fn [db]
      (when-not (s/valid? ::specs/app-db db)
        (.error js/console "SPEC FAILED" (::s/problems (s/explain-data ::specs/app-db db)))))))

(def debug-logger
  (rf/->interceptor
    :id :debug-logger
    :after
    (fn [{:keys [effects coeffects] :as context}]
      (let [sheet-id (sel/current-sheet-id (:db effects))
            new-db (:db effects)
            old-db (:db coeffects)
            event (:event coeffects)
            group-name (str "Dispatch: " (first event))]
        (.groupCollapsed js/console group-name)
        (.info js/console "%c Event" "color: #03A9F4; font-weight: bold" event)
        (if new-db
          (do
            (.info js/console "%c New DB" "color: #9E9E9E; font-weight: bold" (sort new-db))
            (.info js/console "%c changes" "color: #FF6259; font-weight: bold"
              (diffp (update old-db :db/sheets-datascript sheet-2/pull-all)
                (update new-db :db/sheets-datascript sheet-2/pull-all) :db/id)))
          (.info js/console "No db changes"))
        (.groupEnd js/console group-name "color: grey"))
      context)))

;; App interceptors
;; ================

(def sync-browser-url
  (rf/->interceptor
    :id :browser-url
    :after
    (fn [context]
      (let [prev-route (get-in context [:coeffects :db :db/active-route])
            next-route (get-in context [:effects :db :db/active-route])]
        (if (and (not= prev-route next-route)
              prev-route
              next-route)
          (router/redirect-to (router/path-for next-route))))
      context)))

(def sheet-tx-sync
  (rf/->interceptor
    :id :sheet-tx-sync
    :after
    (fn [{:keys [effects coeffects] :as context}]
      (let [event (get-in coeffects [:event 0])]
        (if-not (= "sheet" (namespace event))
          context
          (let [new-sheet (sel/sheet (:db effects))
                old-sheet (sel/sheet (:db coeffects))]
            (if (= new-sheet old-sheet)
              context
              (assoc-in context [:effects :socket :sync-sheet]
                [:sheets/update {:sheet-id (:db/id new-sheet)
                                 :diff (diffp old-sheet new-sheet :db/id)}]))))))))

(def APP_MIDDLEWARE [sync-browser-url])
(def DEV_MIDDLEWARE [debug-logger spec-checker])
(def MIDDLEWARE
  (into APP_MIDDLEWARE (when ^boolen goog.DEBUG DEV_MIDDLEWARE)))

(defn reg-event-fx [id & args]
  (apply rf/reg-event-fx id MIDDLEWARE args))

(defn reg-event-db [id handler]
  (rf/reg-event-db id MIDDLEWARE handler))
